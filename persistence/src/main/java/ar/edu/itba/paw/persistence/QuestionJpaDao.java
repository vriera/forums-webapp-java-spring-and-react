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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Primary
@Repository
public class QuestionJpaDao implements QuestionDao {

    @PersistenceContext
    EntityManager em;

    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionJpaDao.class);

    private static final String QUESTION_IDS = "questionIds";
    private static final String GET_QUESTION_FROM_QUESTION_IDS = "from Question where id IN :questionIds";


    @Override
    public Optional<Question> findById(Long id)
    {
        Question q = em.find(Question.class, id) ;
        if ( q == null)
            return Optional.empty();

        q.setVotes(getTotalVotesByQuestionId(q.getId()));

        return Optional.ofNullable(q);
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

        final TypedQuery<Question> query = em.createQuery(GET_QUESTION_FROM_QUESTION_IDS, Question.class);
        query.setParameter(QUESTION_IDS, questionIds.stream().map(Long::new).collect(Collectors.toList()));

        return query.getResultList().stream().collect(Collectors.toList());
    }


    @Override
    @Transactional
    public Optional<Question> updateImage(Number questionId , Number imageId) {
        final Query query;
        query = em.createQuery("update Question as q set q.imageId = :imageId where q.id = :id");
        query.setParameter("id", questionId.longValue());
        query.setParameter("imageId", imageId.longValue());
        Integer resultId = query.executeUpdate();

        LOGGER.debug("UPDATED IMAGE: {}", resultId);
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

        final TypedQuery<Question> query = em.createQuery(GET_QUESTION_FROM_QUESTION_IDS, Question.class);
        query.setParameter(QUESTION_IDS, questionIds.stream().map(Long::new).collect(Collectors.toList()));

        return query.getResultList().stream().collect(Collectors.toList());
    }

    @Transactional
    @Override
    public Question create(String title , String body , User owner, Forum forum , Long imageId) {
        Question q = new Question(null , new Timestamp(System.currentTimeMillis()), title , body , owner , forum , imageId);
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

        final TypedQuery<Question> query = em.createQuery(GET_QUESTION_FROM_QUESTION_IDS, Question.class);
        query.setParameter(QUESTION_IDS, questionIds.stream().map(Long::new).collect(Collectors.toList()));

        return new ArrayList<>(query.getResultList());
    }

    @Override
    public int findByUserCount(long userId) {
        final Query query = em.createQuery("select count(q) from Question as q where q.owner.id = :userId");
        query.setParameter("userId" , userId);
        return Integer.parseInt(query.getSingleResult().toString());
    }

    @Override
    @Transactional
    public void addVote(Boolean vote, User user, Long questionId) {
        Optional<Question> questionOptional = findById(questionId);
        questionOptional.ifPresent( question -> {
            boolean present = false;
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

    @Override
    public int getTotalVotesByQuestionId(Long questionId) {
        Long result = em.createQuery("SELECT SUM(CASE WHEN qv.vote = TRUE THEN 1 ELSE -1 END) FROM QuestionVotes qv WHERE qv.question.id = :questionId", Long.class)
                .setParameter("questionId", questionId)
                .getSingleResult();

        return result != null ? result.intValue() : 0;
    }



    //Question votes
    @Override
    public List<QuestionVotes> findVotesByQuestionId(Long questionId, int limit, int offset) {

        final String select = "SELECT qv.votes_id FROM questionVotes qv WHERE qv.question_id = :id";
        Query nativeQuery = em.createNativeQuery(select);
        nativeQuery.setParameter("id" , questionId);
        nativeQuery.setFirstResult(offset);
        nativeQuery.setMaxResults(limit);

        @SuppressWarnings("unchecked")
        final List<Long> votesIds = (List<Long>) nativeQuery.getResultList().stream().map(e -> Long.valueOf(e.toString())).collect(Collectors.toList());
        if(votesIds.isEmpty())
            return Collections.emptyList();

        final TypedQuery<QuestionVotes> query = em.createQuery("from QuestionVotes where id IN :votesIds", QuestionVotes.class);
        query.setParameter("votesIds", votesIds);
        return query.getResultList();

    }

    @Override
    public int findVotesByQuestionIdCount(Long questionId) {
        Long result =  em.createQuery("SELECT count(qv) FROM QuestionVotes qv WHERE qv.question.id = :questionId", Long.class)
                .setParameter("questionId", questionId)
                .getSingleResult();
        return result == null? 0 : result.intValue();
    }

}
