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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Primary
@Repository
public class AnswersJpaDao implements AnswersDao {

    private static final String ANSWER_IDS = "answerIds";

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Answer> findById(long id) {
        return Optional.ofNullable(em.find(Answer.class, id));

    }

    @Override
    public List<Answer> getAnswers(int limit, int offset) {
        final String select = "SELECT answer.answer_id from answer order by (case when answer.verify = true then 1 else 2 end)";
        Query nativeQuery = em.createNativeQuery(select);
        nativeQuery.setFirstResult(offset); //offset
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
    public List<Answer> findByQuestion(Long question, int limit, int offset) {

        final String select = "SELECT answer.answer_id from answer where answer.question_id = :id order by (case when answer.verify = true then 1 else 2 end)";
        Query nativeQuery = em.createNativeQuery(select);
        nativeQuery.setParameter("id", question);
        nativeQuery.setFirstResult(limit*offset); //offset
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
    public int findByQuestionCount(Long question) {
        final Query queryTotal = em.createQuery("Select count(distinct id) from Answer as a where a.question.id = :question");
        queryTotal.setParameter("question", question);
        return Integer.parseInt(queryTotal.getSingleResult().toString());
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
    public int deleteAnswer(Long id) {
        Optional<Answer> answer = findById(id);
        if(!answer.isPresent()){
            return -1;
        }
        em.remove(answer.get());
        return 0;
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
}


