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

    //Busca preguntas similares en todas las comunidades y foros
    List<Question> search(String query);

    //Busca preguntas similares en todos los foros dada una comunidad (identificada por su id)
    List<Question> searchByCommunity(String query, Number communityId);

    void addVote(Boolean vote, Long user, Long questionId);

    //Devuelve las preguntas hechas por un cierto usuario
    List<Question> findByUser(long userId, int offset, int limit);

    int findByUserCount(long userId);
}
