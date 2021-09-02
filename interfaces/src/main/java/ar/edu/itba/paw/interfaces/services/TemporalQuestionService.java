package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Question;

public interface TemporalQuestionService {
    Integer addTemporalQuestion( String title , String body, Number community , Number forum );
    Question removeTemporalQuestion( Integer key );
}
