package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.services.TemporalQuestionService;
import ar.edu.itba.paw.models.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;

@Service
public class TemporalQuestionServiceImpl implements TemporalQuestionService {

    private Integer nextKey = 0;
    private HashMap<Integer, Question> temporalQuestions = new HashMap<>();

    @Override
    public Integer addTemporalQuestion( String title , String body, Number community , Number forum ){
        temporalQuestions.put(nextKey , new Question(title,body,community.longValue() ,forum.longValue() ));
        nextKey++;
        return nextKey-1;
    }
    @Override
    public Question removeTemporalQuestion( Integer key ){
        Question aux = temporalQuestions.get(key);
        temporalQuestions.remove(key);
        return aux;
    }

}
