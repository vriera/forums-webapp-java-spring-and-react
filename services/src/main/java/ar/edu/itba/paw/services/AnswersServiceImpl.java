package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.exceptions.BadParamsException;
import ar.edu.itba.paw.interfaces.exceptions.GenericNotFoundException;
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
    public List<Answer> getAnswers(int limit, int page, Long user, Long questionId) throws BadParamsException, GenericNotFoundException {

        if(user!=null){
            if(questionId!=null) throw new BadParamsException("userId and questionId");
            return answerDao.getUserAnswers(user,limit,page);
        }
        if(questionId==null) throw new BadParamsException("empty");
        Optional<Question> question = questionService.findByIdWithoutVotes(questionId);
        if (!question.isPresent())
            throw new GenericNotFoundException("question");
        return answerDao.findByQuestion(questionId,limit,page);
    }


    public Optional<Answer> verify(Long id, boolean bool) throws GenericNotFoundException { //TODO SPRING SECUTIRY: if (answer.get().getQuestion().getOwner().equals(user.get()));
        Optional<Answer> answer = findById(id);
        if (!answer.isPresent()) throw new GenericNotFoundException("answer doesnt exist");
        return answerDao.verify(id, bool);

    }

    @Override
    public Optional<Long> countAnswers(Long question, Long userId) {
        if(userId!=null) return answerDao.countUserAnswers(userId);
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
    public Optional<Answer> create(String body, User user, Long idQuestion, String baseUrl) throws GenericNotFoundException, BadParamsException {
        if(body == null || idQuestion == null) throw new BadParamsException("idQuestion or body");
        Optional<Question> q = questionService.findById(user, idQuestion);

        if(!q.isPresent()) throw new GenericNotFoundException("question");

        Optional<Answer> a = Optional.ofNullable(answerDao.create(body ,user, q.get()));
        //que pasa si no se envia el mail?
            a.ifPresent(answer ->
                    mailingService.sendAnswerVerify(q.get().getOwner().getEmail(), q.get(), answer, baseUrl, LocaleContextHolder.getLocale())
            );

        return a;
    }

    @Override
    @Transactional
    public void answerVote(long answerId, User user, Boolean vote) throws BadParamsException, GenericNotFoundException {
        Optional<Answer> answer = findById(answerId);
        if (!answer.isPresent()) throw new GenericNotFoundException("answer id");
        Optional<Question> q = questionService.findById(user, answer.get().getQuestion().getId());
        answerDao.addVote(vote,user,answer.get().getId());
    }



    private void filterAnswerList(List<Answer> list, User current){
        List<Answer> listVerify = new ArrayList<>();
        List<Answer> listNotVerify = new ArrayList<>();
        int i =0;
        boolean finish = false;
        if (list.isEmpty()){
            return;
        }
        while(!list.isEmpty() && !finish){
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
