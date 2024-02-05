package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistance.QuestionDao;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Forum;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;
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
public class QuestionJpaDaoTest {

    @Autowired
    private QuestionDao questionJpaDao;

    @Test
    public void testFindById() {
        Optional<Question> optionalQuestion = questionJpaDao.findById(1L);
        assertTrue(optionalQuestion.isPresent());
        assertEquals("Question 1", optionalQuestion.get().getTitle());
    }

    @Test
    public void testCreate() {
        User user = new User(1L, "User 1", "user1@test.com", "password");
        Community community = new Community(1L, "Community 1", "Community 1", user);
        Forum forum = new Forum(1L, "Forum 1", community);
        Question question = questionJpaDao.create("Created Question", "Created Question", user, forum, null);
        assertNotNull(question);
        Optional<Question> optionalQuestion = questionJpaDao.findById(question.getId());
        assertTrue(optionalQuestion.isPresent());
        assertEquals("Created Question", optionalQuestion.get().getTitle());
    }
}