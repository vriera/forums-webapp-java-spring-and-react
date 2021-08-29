package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistance.QuestionDao;
import ar.edu.itba.paw.interfaces.services.QuestionService;
import ar.edu.itba.paw.models.Category;
import ar.edu.itba.paw.models.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionDao questionDao;

    @Override
    public Optional<Question> findById(Long id ){
        return questionDao.findById(id);
    };
    @Override
    public List<Question> findAll(){
        return questionDao.findAll();
    };


    @Override

    public List<Question> findByCategory(Category category){
        return questionDao.findByCategory(category);
    };



}
