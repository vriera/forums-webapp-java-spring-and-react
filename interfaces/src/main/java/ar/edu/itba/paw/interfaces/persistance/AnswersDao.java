package ar.edu.itba.paw.interfaces.persistance;

import ar.edu.itba.paw.models.*;

import java.util.List;
import java.util.Optional;

public interface AnswersDao {

    Optional<Answer> findById(long id);

    public List<Answer> findByQuestion(Long question, int limit, int offset);

    Answer create(String body , User owner, Question question);

    Optional<Answer> verify(Long id, boolean bool);

    void addVote(Boolean vote, User user, Long answerId);

    Optional<Long> countAnswers(Long question);

    //Devuelve las respuestas hechas por un cierto usuario
    List<Answer> findByUser(Long userId, int offset, int limit);

    int findByUserCount(Long userId);

    public int deleteAnswer(Long id);
}
