package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistance.AnswersDao;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.Answer;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(AnswersServiceImpl.class);


    @Override
    public Boolean canAccess(User u , long id){
        Optional<Answer> ans = answerDao.findById(id);

        if(!ans.isPresent()) //con null no existe seria not found
            return null;

        Community c = ans.get().getQuestion().getForum().getCommunity();
        return communityService.canAccess(u , c);
    }

    @Override
    public Boolean canAccess(User u , Answer a ){
        return canAccess(u , a.getId());
    }

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
    public Optional<Answer> create(String body, String email, Long idQuestion, String baseUrl)  {
        if(body == null || idQuestion == null || email == null )
            return Optional.empty();

        Optional<User> u = userService.findByEmail(email);
        Optional<Question> q = questionService.findById(u.orElse(null), idQuestion);

        if(!q.isPresent() || !u.isPresent()) return Optional.empty();

        Optional<Answer> a = Optional.ofNullable(answerDao.create(body ,u.get(), q.get()));
        //que pasa si no se envia el mail?
            a.ifPresent(answer ->
                    mailingService.sendAnswerVerify(q.get().getOwner().getEmail(), q.get(), answer, baseUrl, LocaleContextHolder.getLocale())
            );

        return a;
    }

    @Override
    @Transactional
    public void answerVote(Answer answer, Boolean vote, String email) {
        if(answer == null) return;
        Optional<User> u = userService.findByEmail(email);
        if(email == null  || !u.isPresent()) return;
        answerDao.addVote(vote,u.get(),answer.getId());
    }

    private void filterAnswerList(List<Answer> list, User current){
        List<Answer> listVerify = new ArrayList<>();
        List<Answer> listNotVerify = new ArrayList<>();
        int i =0;
        boolean finish = false;

        if (list.isEmpty()){
            LOGGER.warn("Filtering empty list");
            return;
        }

        while(!finish){
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
        list.sort((o1, o2) -> Integer.compare(o2.getVotes(),o1.getVotes()));
    }

}
