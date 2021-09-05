package ar.edu.itba.paw.interfaces.persistance;

import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Forum;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface QuestionDao {

    Optional<Question> findById(long id);

    List<Question> findAll();

    List<Question> findByForum(Number community_id, Number forum_id);

    Question create(String title , String body , User owner, Forum forum);
}
