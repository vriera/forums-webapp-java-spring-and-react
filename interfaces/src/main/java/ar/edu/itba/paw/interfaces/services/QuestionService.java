package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.interfaces.exceptions.GenericOperationException;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface QuestionService {

    List<Question> findAll(User requester, int page);

    Optional<Question> findById(User requester, long id);

    public List<Question> findByUser(Long userId, int page, int limit);

    public int  findByUserCount(Long userId);
    Optional<Question> findByIdWithoutVotes(long id );

    List<Question> findByForum(User requester, Number communityId, Number forumId, int limit, int offset);

    Optional<Question> create(String title , String body , User owner, Long communityId , byte[] image) throws GenericOperationException;

    Boolean questionVote(Question question, Boolean vote, String email);

}
