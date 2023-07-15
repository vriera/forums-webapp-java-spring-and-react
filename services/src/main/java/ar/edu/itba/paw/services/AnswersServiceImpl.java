package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistance.AnswersDao;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class AnswersServiceImpl implements AnswersService {


    private final static int PAGE_SIZE = 10;
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
    public Optional<AnswerVotes> getAnswerVote(Long id, Long userId){

        Optional<Answer> answer = answerDao.findById(id);
        if(!answer.isPresent())
            return Optional.empty();
        return answer.get().getAnswerVotes().stream().filter( x -> x.getOwner().getId() == userId).findFirst();
    }

    @Override
    public List<Answer> findByQuestion(Long questionId, int page){
        boolean idIsInvalid = questionId == null || questionId < 0;
        boolean pageIsInvalid = page < 0;
        if( idIsInvalid || pageIsInvalid )
            return Collections.emptyList();

        List<Answer> list = answerDao.findByQuestion(questionId, PAGE_SIZE, page * PAGE_SIZE);
        return list;
    }
    @Override
    public int findByQuestionCount(Long questionId) {

        int count = answerDao.findByQuestionCount(questionId);
        int mod = count % PAGE_SIZE;
        return mod != 0 ? (count / PAGE_SIZE) + 1 : count / PAGE_SIZE;
    }


    public Optional<Answer> verify(Long id, boolean bool){
        return answerDao.verify(id, bool);
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
    public Optional<Answer> create(String body, String email, Long questionId, String baseUrl)  {
        if(body == null || questionId == null || email == null )
            return Optional.empty();

        Optional<User> u = userService.findByEmail(email);
        Optional<Question> q = questionService.findById( questionId);

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
    public Boolean answerVote(Answer answer, Boolean vote, String email) {
        if(answer == null) return false;

        Optional<User> u = userService.findByEmail(email);

        if(email == null  || !u.isPresent()) return false;

        //TODO: esto lo resulve ya el spring security... Esta bien?
       if(!communityService.canAccess(u.get(),answer.getQuestion().getForum().getCommunity())) return false;

        answerDao.addVote(vote,u.get(),answer.getId());
        return true;
    }




    //Vote lists


    @Override
    public List<AnswerVotes> findVotesByAnswerId(Long answerId , Long userId , int page){
        if(!(userId == null || userId <0)){
            List<AnswerVotes> answerVotesList = new ArrayList<>();
            if(page == 0)
                getAnswerVote(answerId , userId).ifPresent(answerVotesList::add);
            return  answerVotesList;
        }
        return answerDao.findVotesByAnswerId(answerId, PAGE_SIZE , page*PAGE_SIZE);
    }

    @Override
    public int findVotesByAnswerIdCount(Long answerId , Long userId){
        if(!(userId == null || userId <0)){

            Optional<AnswerVotes> vote = getAnswerVote(answerId , userId);
            if(vote.isPresent())
                return  1;
            return 0;
        }
        int count = answerDao.findVotesByAnswerIdCount(answerId);

        int mod = count % PAGE_SIZE;
        return mod != 0 ? (count / PAGE_SIZE) + 1 : count / PAGE_SIZE;
    }

}
