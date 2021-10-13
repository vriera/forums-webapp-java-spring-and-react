package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.*;

import java.util.List;
import java.util.Optional;

public interface QuestionService {

    List<Question> findAll(int limit, int offset);

    Optional<Question> findById(long id);

    List<Question> findByForum(Number community_id, Number forum_id, int limit, int offset);

    Optional<Question> create(String title , String body , User owner, Forum forum , byte[] image);

    Optional<Question> create(String title, String body, String ownerEmail, Number forumId , byte[] image );

    Optional<Question> questionVote(Long idQuestion, Boolean vote, String email);


}
