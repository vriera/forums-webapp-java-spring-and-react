package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistance.AnswersDao;
import ar.edu.itba.paw.models.Answer;
import ar.edu.itba.paw.models.AnswerVotes;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import java.util.*;
import java.util.stream.Collectors;

@Primary
@Repository
public class AnswersJpaDao implements AnswersDao {

    private static final String ANSWER_IDS = "answerIds";

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Answer> findById(long id) {
        Answer a = em.find(Answer.class , id);
        if(a==null)
            return Optional.empty();
        a.setVotes(getTotalVotesByAnswerId(a.getId()));
        return Optional.ofNullable(a);

    }

    @Override
    public List<Answer> findByQuestion(Long question, int limit, int offset) {

        final String select = "SELECT answer.answer_id from answer left join answerVotes on (answer.answer_id = answerVotes.answer_id ) where answer.question_id = :id group by answer.answer_id order by sum((case when coalesce(answer.verify,false) = true then 1 else 2 end)) , SUM(CASE WHEN answerVotes.vote = TRUE THEN 1 WHEN answerVotes.vote is NULL THEN 0 ELSE -1 END) DESC , answer.time  DESC ";
        Query nativeQuery = em.createNativeQuery(select);
        nativeQuery.setParameter("id", question);
        nativeQuery.setFirstResult(offset); //offset
        nativeQuery.setMaxResults(limit);

        @SuppressWarnings("unchecked")
        final List<Long> answerIds = (List<Long>) nativeQuery.getResultList().stream().map(e -> Long.valueOf(e.toString())).collect(Collectors.toList());

        if(answerIds.isEmpty()){
            return Collections.emptyList();
        }

        final TypedQuery<Answer> query = em.createQuery("from Answer where id IN :answerIds", Answer.class);
        query.setParameter(ANSWER_IDS, answerIds);
        List<Answer> result = query.getResultList();

        if(result.isEmpty())
            return Collections.emptyList();

        for(Answer answer: result){
            answer.setVotes(getTotalVotesByAnswerId(answer.getId()));

        }


        result.sort(  Comparator.comparing(Answer::getVerify, Comparator.reverseOrder())
                .thenComparing(Answer::getVotes, Comparator.reverseOrder())
                .thenComparing(Answer::getTime , Comparator.reverseOrder()));

        return result;
    }


    @Override
    public long findByQuestionCount(Long question) {
        final Query queryTotal = em.createQuery("Select count(distinct id) from Answer as a where a.question.id = :question");
        queryTotal.setParameter("question", question);
        return Long.parseLong(queryTotal.getSingleResult().toString());
    }


    @Override
    @Transactional
    public Answer create(String body, User owner, Question question) {
        Answer answer = new Answer(null, body, false, question, owner,new Date());
        em.persist(answer);
        return answer;
    }



    @Override
    public List<Answer> findByUser(Long userId, int limit, int offset) {

        final String select = "SELECT answer.answer_id from answer where answer.user_id = :id order by (case when answer.verify = true then 1 else 2 end)";
        Query nativeQuery = em.createNativeQuery(select);
        nativeQuery.setParameter("id", userId);
        nativeQuery.setFirstResult(offset);
        nativeQuery.setMaxResults(limit);

        @SuppressWarnings("unchecked")
        final List<Integer> answerIds = nativeQuery.getResultList();

        if(answerIds.isEmpty()){
            return Collections.emptyList();
        }

        final TypedQuery<Answer> query = em.createQuery("from Answer where id IN :answerIds order by (case when verify = true then 1 else 2 end)", Answer.class);
        query.setParameter(ANSWER_IDS, answerIds.stream().map(Long::new).collect(Collectors.toList()));

        return query.getResultList().stream().collect(Collectors.toList());
    }

    @Override
    public Optional<Long> findByUserCount(Long userId) {
        final Query queryTotal = em.createQuery("Select count(distinct id) from Answer where owner.id = :userId");
        queryTotal.setParameter("userId", userId);
        return Optional.ofNullable((Long)queryTotal.getSingleResult());
    }

    @Override
    @Transactional
    public void deleteAnswer(Long id) {
        Optional<Answer> answer = findById(id);
        em.remove(answer.orElseThrow(NoSuchElementException::new));
    }


    @Override
    @Transactional
    public Optional<Answer> verify(Long id, boolean bool) {
        Optional<Answer> answerOptional = findById(id);
        answerOptional.ifPresent(answer -> {
            answer.setVerify(bool);
            em.persist(answer);
        });
        return answerOptional;
    }

    @Override
    @Transactional
    public void addVote(Boolean vote, User user, Long answerId) {
        Optional<Answer> answerOptional = findById(answerId);
        answerOptional.ifPresent(answer -> {
            boolean present = false;
            for(AnswerVotes av : answer.getAnswerVotes()){
                if(av.getOwner().equals(user)){
                    av.setVote(vote);
                    present = true;
                    em.persist(av);
                }
            }
            if(!present){
                AnswerVotes av = new AnswerVotes(null,vote,user,answer);
                em.persist(av);
            }
            em.persist(answer);
        });

    }


    public int getTotalVotesByAnswerId(Long answerId) {
        String jpql = "SELECT SUM(CASE WHEN av.vote = TRUE THEN 1 ELSE -1 END) FROM AnswerVotes av WHERE av.answer.id = :answerId";
        Long result = em.createQuery(jpql, Long.class)
                .setParameter("answerId", answerId)
                .getSingleResult();
        return result != null ? result.intValue() : 0;
    }




    //vote lists

    @Override
    public List<AnswerVotes> findVotesByAnswerId(Long answerId , int limit , int offset){
        final String select = "SELECT av.votes_id FROM answerVotes av WHERE av.answer_id = :id";
        Query nativeQuery = em.createNativeQuery(select);
        nativeQuery.setParameter("id" , answerId);
        nativeQuery.setFirstResult(offset);
        nativeQuery.setMaxResults(limit);

        @SuppressWarnings("unchecked")
        final List<Long> votesIds = (List<Long>) nativeQuery.getResultList().stream().map(e -> Long.valueOf(e.toString())).collect(Collectors.toList());
        if(votesIds.isEmpty())
            return Collections.emptyList();

        final TypedQuery<AnswerVotes> query = em.createQuery("from AnswerVotes where id IN :votesIds", AnswerVotes.class);
        query.setParameter("votesIds", votesIds);
        return query.getResultList();

    }

    @Override
    public int findVotesByAnswerIdCount(Long answerId ){
        final TypedQuery<Long> query = em.createQuery("SELECT count(av) from AnswerVotes av where av.answer.id = :answerId", Long.class);
        query.setParameter("answerId", answerId);
        Long val = query.getSingleResult();
        return val != null? val.intValue() : 0;
    }
}


