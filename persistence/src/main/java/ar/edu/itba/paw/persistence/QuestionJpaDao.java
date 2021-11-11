package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistance.QuestionDao;
import ar.edu.itba.paw.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Override
    @Transactional
    public void addVote(Boolean vote, User user, Long questionId) {
        Optional<Question> questionOptional = findById(questionId);
        questionOptional.ifPresent((question) -> {
            Boolean present = false;
            for(QuestionVotes qv : question.getQuestionVotes()){
                if(qv.getOwner().equals(user)){
                    qv.setVote(vote);
                    present = true;
                    em.persist(qv);
                }
            }
            if(!present){
                QuestionVotes qv = new QuestionVotes(null,vote,user,question);
                em.persist(qv);
            }



            em.persist(question);
        });

    }

}