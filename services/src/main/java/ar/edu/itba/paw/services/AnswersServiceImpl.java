package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistance.AnswersDao;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.Answer;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class AnswersServiceImpl implements AnswersService {

    @Autowired
    private AnswersDao answerDao;

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private MailingService mailingService;

    @Autowired
    private CommunityService communityService;



    @Override
    public List<Answer> findByQuestion(Long idQuestion, int limit, int offset, User current){
        List<Answer> list = answerDao.findByQuestion(idQuestion, limit, offset);
        filterAnswerList(list,current);
        return list;
    }

    @Override
    public List<Answer> getAnswers(int limit, int page, User current) {
        List<Answer> list = answerDao.getAnswers(limit,page);
        filterAnswerList(list,current);
        return list;
    }


    public Optional<Answer> verify(Long id, boolean bool){
        return answerDao.verify(id, bool);
    }

    @Override
    public Optional<Long> countAnswers(long question) {
        return answerDao.countAnswers(question);
    }

    @Override
    public void deleteAnswer(Long id) {
        answerDao.deleteAnswer(id);
    }

    @Override
    public Optional<Answer> findById(Long id) {
        return answerDao.findById(id);
    }

   @Override
   @Transactional
    public Optional<Answer> create(String body, String email, Long idQuestion, String baseUrl) {
        if(body == null || idQuestion == null || email == null )
            return Optional.empty();

        Optional<User> u = userService.findByEmail(email);
        Optional<Question> q = questionService.findById(u.orElse(null), idQuestion);

        //Si no tiene acceso a la comunidad, no quiero que pueda responder
        if(!q.isPresent() || !u.isPresent() || !communityService.canAccess(u.get(), q.get().getForum().getCommunity()))
            return Optional.empty();

        Optional<Answer> a = Optional.ofNullable(answerDao.create(body ,u.get(), q.get()));
        //que pasa si no se envia el mail?
            a.ifPresent(answer ->
                    mailingService.sendAnswerVerify(q.get().getOwner().getEmail(), q.get(), answer, baseUrl, LocaleContextHolder.getLocale())
            );

        return a;
    }

    @Override
    @Transactional
    public Boolean answerVote(Answer answer, Boolean vote, String email) {
        if(answer == null ||  email == null)
            return null;
        Optional<User> u = userService.findByEmail(email);

        if(!u.isPresent())
            return null; //no puede votar
        Optional<Question> q = questionService.findById(u.get(), answer.getQuestion().getId());
        if(q.isPresent())
            System.out.println("checking question: " + q.get().getId() + "on forum: " + q.get().getForum().getId() + " on community: " + q.get().getForum().getCommunity().getId());
        if(!q.isPresent() || !communityService.canAccess(u.get(), q.get().getForum().getCommunity())) //Si no tiene acceso a la comunidad, no quiero que pueda votar la respuesta
            return false;
        answerDao.addVote(vote,u.get(),answer.getId());
        return true;
    }



    private void filterAnswerList(List<Answer> list, User current){
        List<Answer> listVerify = new ArrayList<>();
        List<Answer> listNotVerify = new ArrayList<>();
        int i =0;
        boolean finish = false;
        if (list.size() == 0 ){
            return;
        }
        while(list.size() > 0 && !finish){
            Answer a = list.remove(i);
            if(current!=null) a.getAnswerVote(current);
            Boolean verify = a.getVerify();
            if(verify != null && verify){
                listVerify.add(a);
            }else{
                listNotVerify.add(a);
                for(Answer ans : list){
                    if(current!=null) ans.getAnswerVote(current);
                    listNotVerify.add(ans);
                }
                list.clear();
                finish = true;
            }
        }
        orderList(listVerify);
        orderList(listNotVerify);

        list.addAll(listVerify);
        list.addAll(listNotVerify);

    }

    private void orderList(List<Answer> list){
        list.sort(new Comparator<Answer>() {
            @Override
            public int compare(Answer o1, Answer o2) {
                return Integer.compare(o2.getVote(),o1.getVote());
            }
        });
    }

}
