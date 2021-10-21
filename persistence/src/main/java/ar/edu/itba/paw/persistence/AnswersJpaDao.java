package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistance.AnswersDao;
import ar.edu.itba.paw.models.Answer;
import ar.edu.itba.paw.models.AnswerVotes;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class AnswersJpaDao implements AnswersDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Answer> findById(long id) {
        return Optional.ofNullable(em.find(Answer.class, id));

    }

    @Override
    public List<Answer> findByQuestion(Long question, int limit, int offset) {
        final TypedQuery<Answer> query = em.createQuery("from Answer as a where a.question.id = :question order by (case when verify = true then 1 else 2 end)", Answer.class);
        query.setParameter("question", question);
        query.setFirstResult(offset);
        query.setMaxResults(limit);

        List<Answer> list = query.getResultList().stream().collect(Collectors.toList());
        return list;
    }

    @Override
    @Transactional
    public Answer create(String body, User owner, Question question) {
        Answer answer = new Answer(null, body, false, question, owner);
        em.persist(answer);
        return answer;
    }


    @Override
    public Optional<Long> countAnswers(Long question) {
        final Query queryTotal = em.createQuery("Select count(distinct id) from Answer as a where a.question.id = :question");
        queryTotal.setParameter("question", question);
        return Optional.ofNullable((Long)queryTotal.getSingleResult());
    }

    @Override
    public List<Answer> findByUser(Long userId, int offset, int limit) {
        final TypedQuery<Answer> query = em.createQuery("from Answer where Answer.owner.id = :userId order by (case when verify = true then 1 else 2 end)", Answer.class);
        query.setParameter("userId", userId);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.getResultList().stream().collect(Collectors.toList());
    }

    @Override
    public int findByUserCount(Long userId) {
        final Query queryTotal = em.createQuery("Select count(distinct id) from Answer where Answer.owner.id = :userId");
        queryTotal.setParameter("userId", userId);
        return (Integer)queryTotal.getSingleResult();
    }


    @Override
    @Transactional
    public Optional<Answer> verify(Long id, boolean bool) {
        Optional<Answer> answerOptional = findById(id);
        answerOptional.ifPresent((answer) -> {
            answer.setVerify(bool);
            em.persist(answer);
        });
        return answerOptional;
    }

    @Override
    @Transactional
    public void addVote(Boolean vote, User user, Long answerId) {
        Optional<Answer> answerOptional = findById(answerId);
        answerOptional.ifPresent((answer) -> {
            Boolean present = false;
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


