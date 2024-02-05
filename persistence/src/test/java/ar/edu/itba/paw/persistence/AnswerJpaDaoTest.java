package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistance.AnswersDao;
import ar.edu.itba.paw.models.Answer;
import ar.edu.itba.paw.models.Question;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Rollback
@Transactional
public class AnswerJpaDaoTest {

    @Autowired
    private AnswersDao answerJpaDao;

    @Test
    public void testFindById() {
        Optional<Answer> answer = answerJpaDao.findById(Utils.TEST_ANSWERS.get(0).getId());
        assertTrue(answer.isPresent());
        assertEquals(Utils.TEST_ANSWERS.get(0).getBody(), answer.get().getBody());
    }

    @Test
    public void testCountAnswers() {
        Question q = Utils.TEST_ANSWERS.get(0).getQuestion();
        long c = 0L;
        for (Answer answer : Utils.TEST_ANSWERS) {
            if (answer.getQuestion().getId().equals(q.getId())) {
                c++;
            }
        }
        Optional<Long> count = answerJpaDao.countAnswers(Utils.TEST_ANSWERS.get(0).getQuestion().getId());
        assertTrue(count.isPresent());
        assertEquals(c, count.get().longValue());
    }

    @Test
    public void testCreate() {
        Answer answer = answerJpaDao.create("Created Answer", Utils.TEST_USERS.get(0), Utils.TEST_ANSWERS.get(0).getQuestion());
        assertNotNull(answer);
        Optional<Answer> optionalAnswer = answerJpaDao.findById(answer.getId());
        assertTrue(optionalAnswer.isPresent());
        assertEquals("Created Answer", optionalAnswer.get().getBody());
    }

}
