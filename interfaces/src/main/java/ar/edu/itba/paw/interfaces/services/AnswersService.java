package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.*;

import java.util.List;
import java.util.Optional;

public interface AnswersService {

    public Optional<Answer> findById(long id);

    public List<Answer> findByQuestionId(long idQuestion);

    Optional<Answer> create(String body , User owner, Long idQuestion);

    Optional<Answer> create(Question question);

}
