package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.*;

import java.util.List;
import java.util.Optional;

public interface AnswersService {
    Optional<Answer> findById(long id );

    Optional<Answer> create(String title , String body , User owner, Long idQuestion);

    Optional<Answer> create(Question question);

}
