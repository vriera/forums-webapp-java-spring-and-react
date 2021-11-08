package ar.edu.itba.paw.interfaces.persistance;

import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Forum;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface QuestionDao {

    Optional<Question> findById(Long id);

    List<Question> findAll(int limit, int offset);

    List<Question> findByForum(Number community_id, Number forum_id, int limit, int offset);

    Question create(String title , String body , User owner, Forum forum , Long imageId);


    //Devuelve las preguntas hechas por un cierto usuario
    List<Question> findByUser(long userId, int offset, int limit);

    int findByUserCount(long userId);

    void addVote(Boolean vote, User user, Long questionId);
}
