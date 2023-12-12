package ar.edu.itba.paw.interfaces.persistance;

import ar.edu.itba.paw.models.*;

import java.util.List;
import java.util.Optional;

public interface QuestionDao {

    Optional<QuestionVotes> findVote(long questionId , long userId);

    Optional<Question> findById(long questionId);
    Question create(String title , String body , User owner, Forum forum , Long imageId);

    List<Question> findByUser(long userId, int offset, int limit);

    long findByUserCount(long userId);

    void addVote(Boolean vote, User user, long questionId);

    long getTotalVotesByQuestionId(long questionId);

    List<QuestionVotes> findVotesByQuestionId(long questionId, int limit, int offset);

    long findVotesByQuestionIdCount(long questionId);
}
