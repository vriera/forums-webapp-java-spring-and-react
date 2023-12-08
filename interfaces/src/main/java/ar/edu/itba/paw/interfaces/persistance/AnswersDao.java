package ar.edu.itba.paw.interfaces.persistance;

import ar.edu.itba.paw.models.Answer;
import ar.edu.itba.paw.models.AnswerVotes;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface AnswersDao {

    Optional<Answer> findById(long id);

    List<Answer> findByQuestion(Long question, int limit, int offset);

    Answer create(String body , User owner, Question question);

    Optional<Answer> verify(Long id, boolean bool);

    void addVote(Boolean vote, User user, Long answerId);


    long findByQuestionCount(Long question);

    //Devuelve las respuestas hechas por un cierto usuario
    List<Answer> findByUser(Long userId, int limit, int offset);

    Optional<Long>  findByUserCount(Long userId);

    void deleteAnswer(Long id);



    List<AnswerVotes> findVotesByAnswerId(Long answerId ,int limit , int offset);

    int findVotesByAnswerIdCount(Long answerId);
}
