package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistance.QuestionDao;
import ar.edu.itba.paw.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Primary
@Repository
public class QuestionJpaDao implements QuestionDao {

    @PersistenceContext
    private EntityManager em;

    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionJpaDao.class);


    @Override
    public Optional<Question> findById(Long id) {
        return Optional.ofNullable(em.find(Question.class, id));
    }

    @Override
    public List<Question> findAll(int page) {
        final String select = "SELECT question.question_id from question LIMIT 10 OFFSET :OFFSET ";
        Query nativeQuery = em.createNativeQuery(select);
        nativeQuery.setParameter("OFFSET",10*(page-1));

        @SuppressWarnings("unchecked")
        final List<Integer> questionIds = (List<Integer>) nativeQuery.getResultList().stream().map(e -> Integer.valueOf(e.toString())).collect(Collectors.toList());

        if(questionIds.isEmpty()){
            return Collections.emptyList();
        }

        final TypedQuery<Question> query = em.createQuery("from Question where id IN :questionIds", Question.class);
        query.setParameter("questionIds", questionIds.stream().map(Long::new).collect(Collectors.toList()));

        return query.getResultList().stream().collect(Collectors.toList());

       /* TypedQuery<Question> query = em.createQuery("from Question", Question.class);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.getResultList();

        */
    }


    @Override
    @Transactional
    public Optional<Question> updateImage(Number questionId , Number imageId) {
        final Query query;
        query = em.createQuery("update Question as q set q.imageId = :imageId where q.id = :id");
        query.setParameter("id", questionId.longValue());
        query.setParameter("imageId", imageId.longValue());
        Integer resultId = query.executeUpdate();
        LOGGER.debug("UPDATED IMAGE: " + resultId);
        return findById(resultId.longValue());
    }

    @Override
    public List<Question> findByForum(Number communityId, Number forumId, int limit, int offset) {
        final String select = "SELECT question.question_id from question where question.community_id = :communityId and question.forum_id = :forumId";
        Query nativeQuery = em.createNativeQuery(select);
        nativeQuery.setParameter("communityId", communityId);
        nativeQuery.setParameter("forumId", forumId);
        nativeQuery.setFirstResult(offset);
        nativeQuery.setMaxResults(limit);

        @SuppressWarnings("unchecked")
        final List<Integer> questionIds = (List<Integer>) nativeQuery.getResultList().stream().map(e -> Integer.valueOf(e.toString())).collect(Collectors.toList());

        if(questionIds.isEmpty()){
            return Collections.emptyList();
        }

        final TypedQuery<Question> query = em.createQuery("from Question where id IN :questionIds", Question.class);
        query.setParameter("questionIds", questionIds.stream().map(Long::new).collect(Collectors.toList()));

        return query.getResultList().stream().collect(Collectors.toList());
        /*
        TypedQuery<Question> query = em.createQuery("from Question as q where q.community.community_id = :community_id and q.forum.forum_id = :forum_id", Question.class);
        query.setParameter("community_id", community_id);
        query.setParameter("forum_id", forum_id);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.getResultList();

         */
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
        final String select = "SELECT question.question_id from question where question.user_id = :userId";
        Query nativeQuery = em.createNativeQuery(select);
        nativeQuery.setParameter("userId", userId);
        nativeQuery.setFirstResult(offset);
        nativeQuery.setMaxResults(limit);

        @SuppressWarnings("unchecked")
        final List<Integer> questionIds = (List<Integer>) nativeQuery.getResultList().stream().map(e -> Integer.valueOf(e.toString())).collect(Collectors.toList());

        if(questionIds.isEmpty()){
            return Collections.emptyList();
        }

        final TypedQuery<Question> query = em.createQuery("from Question where id IN :questionIds", Question.class);
        query.setParameter("questionIds", questionIds.stream().map(Long::new).collect(Collectors.toList()));

        return query.getResultList().stream().collect(Collectors.toList());

        /*
        TypedQuery<Question> query = em.createQuery("from Question as q where q.owner.id = :userId", Question.class);
        query.setParameter("userId", userId);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.getResultList();

         */
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
