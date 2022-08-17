package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Forum;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface QuestionService {

    List<Question> findAll(User requester, int page);

    Optional<Question> findById(User requester, long id);

    List<Question> findByForum(User requester, Number community_id, Number forum_id, int limit, int offset);

    Optional<Question> create(String title , String body , User owner, Forum forum , byte[] image);

    Optional<Question> create(String title, String body, String ownerEmail, Integer forumId , byte[] image );

    Optional<Question> questionVote(Long idQuestion, Boolean vote, String email);
}
