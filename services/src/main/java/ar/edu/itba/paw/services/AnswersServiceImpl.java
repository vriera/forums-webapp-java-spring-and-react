package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistance.AnswersDao;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;

import javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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




    //TODO: refactor, hace cualquiera
    @Override
    public AnswerVotes getAnswerVote(Long id, Long userId){

        Optional<Answer> answer = answerDao.findById(id);
        if(!answer.isPresent())
            throw new NoSuchElementException("");
        return answer.get().getAnswerVotes().stream().filter( x -> x.getOwner().getId() == userId).findFirst().orElseThrow(NoSuchElementException::new);
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


    public Answer verify(Long id, boolean bool){
        //TODO: ver si es un error!
        return answerDao.verify(id, bool).orElseThrow(NoSuchElementException::new);
    }



    @Override
    public void deleteAnswer(Long id) {
        answerDao.deleteAnswer(id);
    }

    @Override
    public Answer findById(Long id) {
        return answerDao.findById(id).orElseThrow(NoSuchElementException::new);
    }

   @Override
   @Transactional
    public Answer create(String body, String email, Long questionId, String baseUrl)  {
        //No deberia pasar luego del validation del form
        if(body == null || questionId == null || email == null )
            throw new IllegalArgumentException();
        User u = userService.findByEmail(email);
       //TODO: TIRABA ILLEGAL ARGUMENT, SI NO ENXONTRABA LA QUESTION, QUE DEBERIA SER?
        Question q = questionService.findById( questionId);



        Optional<Answer> a = Optional.ofNullable(answerDao.create(body ,u, q));
        //que pasa si no se envia el mail?
        a.ifPresent(answer ->
                mailingService.sendAnswerVerify(q.getOwner().getEmail(), q, answer, baseUrl, LocaleContextHolder.getLocale())
        );

       //TODO: change name of error
        return a.orElseThrow(RuntimeException::new);
    }

    @Override
    @Transactional
    public Boolean answerVote(Answer answer, Boolean vote, String email) {
        User u = userService.findByEmail(email);

        answerDao.addVote(vote,u,answer.getId());
        return true;
    }




    //Vote lists

    //TODO: add bad request
    @Override
    public List<AnswerVotes> findVotesByAnswerId(Long answerId , Long userId , int page){
        if(!(userId == null || userId <0)){
            List<AnswerVotes> answerVotesList = new ArrayList<>();
            if(page == 0){
                try {
                    answerVotesList.add(getAnswerVote(answerId, userId));
                }catch (NoSuchElementException ignored){}
            }
            return  answerVotesList;
        }
        return answerDao.findVotesByAnswerId(answerId, PAGE_SIZE , page*PAGE_SIZE);
    }

    @Override
    public int findVotesByAnswerIdCount(Long answerId , Long userId){
        if(!(userId == null || userId <0)){
            try {
                getAnswerVote(answerId, userId);
                return 1;
            }catch (NoSuchElementException ignored){}
            return 0;
        }
        int count = answerDao.findVotesByAnswerIdCount(answerId);

        int mod = count % PAGE_SIZE;
        return mod != 0 ? (count / PAGE_SIZE) + 1 : count / PAGE_SIZE;
    }

}
