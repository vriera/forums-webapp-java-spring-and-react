package ar.edu.itba.paw.interfaces.persistance;

import ar.edu.itba.paw.models.*;

import java.util.List;
import java.util.Optional;

public interface AnswersDao {
    Optional<Answer> findById(long id);
    List<Answer> findByQuestion(long idQuestion);
    Answer create(String body , User owner, Long id_question);
    Optional<Answer> verify(Long id);
    void addVote(Boolean vote, Long user, Long answerId);

}
