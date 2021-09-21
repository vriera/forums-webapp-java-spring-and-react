package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Forum;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface QuestionService {

    List<Question> findAll();

    Optional<Question> findById(long id);

    List<Question> findByForum(Number community_id, Number forum_id);

    Optional<Question> create(String title , String body , User owner, Forum forum);

    Optional<Question> create(String title, String body, String ownerEmail, Number forumId);

    Optional<Question> create(Question question);
}
