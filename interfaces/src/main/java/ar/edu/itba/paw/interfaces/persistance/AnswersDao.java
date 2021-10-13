package ar.edu.itba.paw.interfaces.persistance;

import ar.edu.itba.paw.models.*;

import java.util.List;
import java.util.Optional;

public interface AnswersDao {

    Optional<Answer> findById(long id);

    public List<Answer> findByQuestion(long question, int limit, int offset);

    Answer create(String body , User owner, Long id_question);

    Optional<Answer> verify(Long id, boolean bool);

    void addVote(Boolean vote, Long user, Long answerId);

    Optional<Long> countAnswers(long question);

    //Devuelve las respuestas hechas por un cierto usuario
    List<Answer> findByUser(long userId, int offset, int limit);

    int findByUserCount(long userId);
}
