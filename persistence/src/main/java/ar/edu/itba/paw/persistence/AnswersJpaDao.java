package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistance.AnswersDao;
import ar.edu.itba.paw.models.Answer;
import ar.edu.itba.paw.models.User;
import org.springframework.stereotype.Repository;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//@Repository
public class AnswersJpaDao implements AnswersDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Answer> findById(long id) {
        return Optional.ofNullable(em.find(Answer.class,id));

    }

    @Override
    public List<Answer> findByQuestion(long question, int limit, int offset) {
        final TypedQuery<Answer> query = em.createQuery("from Answer where id_question = :question",Answer.class);
      query.setParameter("question",question);
      return query.getResultList().stream().collect(Collectors.toList());
    }

    @Override
    public Answer create(String body, User owner, Long id_question) {
       Answer answer = new Answer(null,body,null,id_question,owner);
       em.persist(answer);
       return answer;
    }

    @Override
    public Optional<Answer> verify(Long id, boolean bool) {
        return Optional.empty();
    }

    @Override
    public void addVote(Boolean vote, Long user, Long answerId) {

    }

    @Override
    public Optional<Long> countAnswers(long question) {
        return Optional.empty();
    }

    @Override
    public List<Answer> findByUser(long userId, int offset, int limit) {
        return null;
    }

    @Override
    public int findByUserCount(long userId) {
        return 0;
    }
}
