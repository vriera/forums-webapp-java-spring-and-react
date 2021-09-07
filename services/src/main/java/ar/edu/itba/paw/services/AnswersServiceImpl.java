package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistance.AnswersDao;
import ar.edu.itba.paw.interfaces.persistance.QuestionDao;
import ar.edu.itba.paw.interfaces.services.AnswersService;
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


    @Override
    public List<Answer> findByQuestionId(long idQuestion) {
        return answerDao.findByQuestion(idQuestion);
    }


    @Override
    public Optional<Answer> findById(long id) {
        return answerDao.findById(id);
    }

    @Override
    public Optional<Answer> create(String body, Long user, Long idQuestion) {
        if(body == null || user == null || idQuestion == null)
            return Optional.empty();

        return Optional.ofNullable(answerDao.create(body ,user, idQuestion));
    }

    @Override
    public Optional<Answer> create(Question question) {
        return Optional.empty();
    }

}
