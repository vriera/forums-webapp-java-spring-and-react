package ar.edu.itba.paw.interfaces.persistance;

import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Forum;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface QuestionDao {

    Optional<Question> findById(long id);

    List<Question> findAll(int limit, int offset);

    List<Question> findByForum(Number community_id, Number forum_id, int limit, int offset);

    Question create(String title , String body , User owner, Forum forum);

    //Busca preguntas similares en todas las comunidades y foros
    List<Question> search(String query, int limit, int offset);

    //Busca preguntas similares en todos los foros dada una comunidad (identificada por su id)
    List<Question> searchByCommunity(String query, Number communityId, int limit, int offset);


    void addVote(Boolean vote, Long user, Long questionId);

    Optional<Long> countQuestions(Number community_id, Number forum_id);

    public Optional<Long> countQuestionsByCommunity(Number community_id, String query);

    public Optional<Long> countQuestionsByCommunity(Number community_id);

    public Optional<Long> countAllQuestions();

    Optional<Long> countQuestionQuery(String query);


}
