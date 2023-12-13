package ar.edu.itba.paw.interfaces.persistance;

import ar.edu.itba.paw.models.Answer;
import ar.edu.itba.paw.models.AnswerVotes;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface AnswersDao {
    Optional<AnswerVotes> findVote(long answerId, long userId);

    Optional<Answer> findById(long id);

    List<Answer> findByQuestion(long question, int limit, int offset);

    Answer create(String body, User owner, Question question);

    Optional<Answer> verify(long id, boolean bool);

    void addVote(Boolean vote, User user, long answerId);

    long findByQuestionCount(long questionId);

    // Devuelve las respuestas hechas por un cierto usuario
    List<Answer> findByUser(long userId, int limit, int offset);

    Optional<Long> findByUserCount(long userId);

    List<AnswerVotes> findVotesByAnswerId(long answerId, int limit, int offset);

    int findVotesByAnswerIdCount(long answerId);
}
