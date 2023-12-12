package ar.edu.itba.paw.interfaces.persistance;

import ar.edu.itba.paw.models.Forum;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.QuestionVotes;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface QuestionDao {

    Optional<Question> findById(long questionId);
    Question create(String title , String body , User owner, Forum forum , Long imageId);

    List<Question> findByUser(long userId, int offset, int limit);

    long findByUserCount(long userId);

    void addVote(Boolean vote, User user, long questionId);

    long getTotalVotesByQuestionId(long questionId);

    List<QuestionVotes> findVotesByQuestionId(long questionId, int limit, int offset);

    long findVotesByQuestionIdCount(long questionId);
}
