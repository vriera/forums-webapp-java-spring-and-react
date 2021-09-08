package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistance.AnswersDao;
import ar.edu.itba.paw.interfaces.persistance.QuestionDao;
import ar.edu.itba.paw.interfaces.services.AnswersService;
import ar.edu.itba.paw.interfaces.services.QuestionService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Answer;
import ar.edu.itba.paw.models.Forum;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class AnswersServiceImpl implements AnswersService {

    @Autowired
    private AnswersDao answerDao;

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionService questionService;



    @Override
    public List<Answer> findByQuestionId(long idQuestion) {
        return answerDao.findByQuestion(idQuestion);
    }

    public Optional<Answer> verify(Long id){
        return answerDao.verify(id);
    }

    @Override
    public Optional<Answer> findById(long id) {
        return answerDao.findById(id);
    }

    @Override
    public Optional<Answer> create(String body, User user, Long idQuestion) {
        if(body == null || user == null || idQuestion == null)
            return Optional.empty();

        return Optional.ofNullable(answerDao.create(body ,user, idQuestion));
    }

    @Override
    public Optional<Answer> create(String body, String username, String email, Long idQuestion) {
        if(body == null || username == null || idQuestion == null || email == null )
            return Optional.empty();
        Optional<User> u = userService.findByEmail(email);
        if(!u.isPresent()){
            u = userService.create(username, email);
        }
        Optional<Question> question = questionService.findById(idQuestion);
        if(question.isPresent()){
            return Optional.ofNullable(answerDao.create(body ,u.get(), idQuestion));
        }
        return null;


    }




}