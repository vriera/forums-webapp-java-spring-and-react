package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.*;

import java.util.List;
import java.util.Optional;

public interface QuestionService {

    List<Question> findAll(User requester, int page);

    Question findById(long id);

    List<Question> findByForum(User requester, Number community_id, Number forum_id, int limit, int offset);

    Question create(String title , String body , User owner, Forum forum , byte[] image);

    Question create(String title, String body, User user, Integer forumId , byte[] image );

    Boolean questionVote(Question question, Boolean vote, User user);

    QuestionVotes getQuestionVote(Long questionId , Long userId);


    List<QuestionVotes> findVotesByQuestionId(Long questionId , Long userId , int page);

    long findVotesByQuestionIdCount(Long questionId, Long userId);

}
