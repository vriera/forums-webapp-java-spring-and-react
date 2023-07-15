package ar.edu.itba.paw.interfaces.persistance;

import ar.edu.itba.paw.models.Forum;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.QuestionVotes;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface QuestionDao {

    Optional<Question> findById(Long id);

    List<Question> findAll(int page);

    List<Question> findByForum(Number community_id, Number forum_id, int limit, int offset);

    Question create(String title , String body , User owner, Forum forum , Long imageId);

    Optional<Question> updateImage(Number questionId , Number imageId);
    //Devuelve las preguntas hechas por un cierto usuario
    List<Question> findByUser(long userId, int offset, int limit);

    int findByUserCount(long userId);

    void addVote(Boolean vote, User user, Long questionId);


    int getTotalVotesByQuestionId(Long questionId);

    List<QuestionVotes> getAllQuestionVotesByQuestionId(Long questionId, int offset, int limit);
    public long getAllQuestionVotesByQuestionIdCount(Long questionId);
}
