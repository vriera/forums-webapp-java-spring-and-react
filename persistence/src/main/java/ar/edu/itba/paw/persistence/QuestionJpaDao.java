package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistance.QuestionDao;
import ar.edu.itba.paw.models.Forum;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.SmartDate;
import ar.edu.itba.paw.models.User;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import org.springframework.cglib.core.Local;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Primary
@Repository
public class QuestionJpaDao implements QuestionDao {

    @PersistenceContext
    EntityManager em;

    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionJpaDao.class);


    @Override
    public Optional<Question> findById(Long id) {
        return Optional.ofNullable(em.find(Question.class, id));
    }

    @Override
    public List<Question> findAll(int limit, int offset) {
        TypedQuery<Question> query = em.createQuery("from Question", Question.class);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    public List<Question> findByForum(Number community_id, Number forum_id, int limit, int offset) {
        TypedQuery<Question> query = em.createQuery("from Question as q where q.community.community_id = :community_id and q.forum.forum_id = :forum_id", Question.class);
        query.setParameter("community_id", community_id);
        query.setParameter("forum_id", forum_id);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Transactional
    @Override
    public Question create(String title , String body , User owner, Forum forum , Long imageId) {
        Question q = new Question(null , new Timestamp(System.currentTimeMillis()), title , body , owner , forum.getCommunity() , forum , imageId);
        em.persist(q);
        return q;
    }


    @Override
    public void addVote(Boolean vote, Long user, Long questionId) {
        //TODO: Natu este es tuyo!
    }

    @Override
    public List<Question> findByUser(long userId, int offset, int limit) {
        TypedQuery<Question> query = em.createQuery("from Question as q where q.owner.id = :userId", Question.class);
        query.setParameter("userId", userId);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    public int findByUserCount(long userId) {
        final Query query = em.createQuery("select count(q) from Question as q where q.owner.id = :userId");
        query.setParameter("userId" , userId);
        return ((Long) query.getSingleResult()).intValue(); //FIXME: El count devuelve un Long, no un Integer!
    }


}
