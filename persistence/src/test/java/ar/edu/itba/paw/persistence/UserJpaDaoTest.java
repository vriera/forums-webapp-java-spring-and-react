package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistance.UserDao;
import ar.edu.itba.paw.models.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Rollback
@Transactional
public class UserJpaDaoTest {

    @Autowired
    private UserDao userJpaDao;


    @Test
    public void testFindByEmail() {
        Optional<User> optionalUser = userJpaDao.findByEmail("user1@test.com");
        assertTrue(optionalUser.isPresent());
        assertEquals("User 1", optionalUser.get().getUsername());
    }

    @Test
    public void testFindByEmailNotFound() {
        Optional<User> optionalUser = userJpaDao.findByEmail("notfound@test.com");
        assertFalse(optionalUser.isPresent());
    }

    @Test
    public void testFindById() {
        Optional<User> optionalUser = userJpaDao.findById(2L);
        assertTrue(optionalUser.isPresent());
        assertEquals("User 2", optionalUser.get().getUsername());
    }

    @Test
    public void testCreate() {
        User user = userJpaDao.create("Created User", "created@test.com", "password");
        assertNotNull(user);
        Optional<User> optionalUser = userJpaDao.findById(user.getId());
        assertTrue(optionalUser.isPresent());
        assertEquals("Created User", optionalUser.get().getUsername());
    }
}