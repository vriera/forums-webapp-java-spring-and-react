package ar.edu.itba.paw.interfaces.persistance;

import ar.edu.itba.paw.models.Answer;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface AnswersDao {

    Optional<Answer> findById(long id);

    List<Answer> getAnswers(int limit, int page);

    List<Answer> getUserAnswers(long userId, int limit, int page);

    List<Answer> findByQuestion(Long question, int limit, int page);

    Answer create(String body , User owner, Question question);

    Optional<Answer> verify(Long id, boolean bool);

    void addVote(Boolean vote, User user, Long answerId);

    Optional<Long> countUserAnswers(Long userId);

    Optional<Long> countAnswers(Long question);

    //Devuelve las respuestas hechas por un cierto usuario
    List<Answer> findByUser(Long userId, int offset, int limit);

    Optional<Long>  findByUserCount(Long userId);

    int deleteAnswer(Long id);
}
