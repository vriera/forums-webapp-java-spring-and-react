package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.*;

import java.util.List;
import java.util.Optional;

public interface QuestionService {

    List<Question> findAll(User requester, int page);

    Optional<Question> findById(long id);

    List<Question> findByForum(User requester, Number community_id, Number forum_id, int limit, int offset);

    Optional<Question> create(String title , String body , User owner, Forum forum , byte[] image);

    Optional<Question> create(String title, String body, User user, Integer forumId , byte[] image );

    Boolean questionVote(Question question, Boolean vote, User user);

    Optional<QuestionVotes> getQuestionVote(Long questionId , Long userId);


}
