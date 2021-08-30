package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistance.QuestionDao;
import ar.edu.itba.paw.interfaces.services.QuestionService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserService userService;

    @Override
    public Optional<Question> findById(Long id ){
        return questionDao.findById(id);
    }

    @Override
    public List<Question> findAll(){
        return questionDao.findAll();
    }

    @Override
    public List<Question> findByCategory(Community community){
        return questionDao.findByCategory(community);
    }

    @Override
    public Optional<Question> create(String title , String body , User owner , Community community){
        Optional<User> user = userService.findByEmail(owner.getEmail());
        if ( user.isPresent()){
           return Optional.of(questionDao.create(title, body, user.get(), community).orElseThrow(NoSuchElementException::new));
        }else {
            owner = userService.create(owner.getUsername() , owner.getEmail()).orElseThrow(NoSuchElementException::new); //Si falla la creación del usuario, hay problemas
            return  Optional.of(questionDao.create(title , body , owner , community).orElseThrow(NoSuchElementException::new));
        }
    }
}
