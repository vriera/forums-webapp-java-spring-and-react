package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.*;

import java.util.List;

public interface QuestionService {
    Question findById(long id);

    Question create(String title, String body, User owner, long communityId, byte[] image);

    Boolean questionVote(long questionId, Boolean vote, User user);

    QuestionVotes getQuestionVote(long questionId, long userId);

    List<QuestionVotes> findVotesByQuestionId(long questionId, Long userId, int page);

    long findVotesByQuestionIdPagesCount(long questionId, Long userId);

}
