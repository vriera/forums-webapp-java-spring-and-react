package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.*;

import java.util.List;
import java.util.Optional;

public interface AnswersService {

    Optional<Answer> findById(long id);

    List<Answer> findByQuestionId(long idQuestion);

    Optional<Answer> create(String body, String email, Long idQuestion);

    Optional<Answer> verify(Long id);

}
