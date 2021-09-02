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

import java.util.NoSuchElementException;
import java.util.Optional;

public class AnswersServiceImpl implements AnswersService {

    @Autowired
    private AnswersDao answerDao;

    @Autowired
    private UserService userService;

    private Integer nextKey = 0;

    @Override
    public Optional<Answer> findById(long id) {
        return answerDao.findById(id);
    }

    @Override
    public Optional<Answer> create(String title, String body, User owner, Long idQuestion) {
        if(title == null || body == null || owner == null || idQuestion == null)
            return Optional.empty();

        Optional<User> user = userService.findByEmail(owner.getEmail());

        if ( user.isPresent()){
            return Optional.ofNullable(answerDao.create(title , body , user.get(), idQuestion));
        }
        else {
            owner = userService.create(owner.getUsername() , owner.getEmail()).orElseThrow(NoSuchElementException::new); //Si tuve un error creando el owner, se rompe
            return  Optional.ofNullable(answerDao.create(title , body , owner, idQuestion));
        }
    }

    @Override
    public Optional<Answer> create(Question question) {
        return Optional.empty();
    }

}
