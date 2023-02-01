package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistance.CommunityDao;
import ar.edu.itba.paw.models.Access;
import ar.edu.itba.paw.models.AccessType;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Rollback
@Transactional
public class CommunityJpaDaoTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private CommunityDao communityJpaDao;

    private long COMMUNITY_ID;
    private final String NAME = "SAMPLE COMMUNITY";
    private final String DESC = "THIS IS A SAMPLE COMMUNITY";
    private Community COMMUNITY;

    private long MOD_ID;
    private final String USERNAME = "USER";
    private final String EMAIL = "example@email.com";
    private final String PASSWORD = "password";
    private User MODERATOR;

    private final String USER_EMAIL = "example+1@email.com";

    @Before
    public void setUp() {
        MODERATOR = new User(null, USER_EMAIL, USERNAME, PASSWORD);
        em.persist(MODERATOR);
        COMMUNITY = new Community(null, NAME, DESC, MODERATOR);
        em.persist(COMMUNITY);
    }

    private Number insertAccess(User user, AccessType type){
        Access access = new Access(null, COMMUNITY, user, type);
        em.persist(access);
        return access.getId();
    }

    private Number insertUser(){
        User user = new User(null, USERNAME, USER_EMAIL, PASSWORD);
        em.persist(user);
        return user.getId();
    }

    private Optional<AccessType> checkAccess(Number userId, Number communityId){
        TypedQuery<AccessType> query = em.createQuery("select a.accessType from Access a where a.user.id = :userId and a.community.id = :communityId", AccessType.class);
        query.setParameter("userId", userId.longValue());
        query.setParameter("communityId", communityId.longValue());
        return query.getResultList().stream().findFirst();
    }

    private Long countRowsInAccess(){
        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = qb.createQuery(Long.class);
        cq.select(qb.count(cq.from(Access.class)));
        return em.createQuery(cq).getSingleResult();
    }

    @Test
    public void testNewAccess(){
        Number userId = insertUser();

        communityJpaDao.updateAccess(userId, COMMUNITY_ID, AccessType.REQUESTED);

        assertEquals(Long.valueOf(1), countRowsInAccess());
    }

    @Test
    public void testDeleteNonexistentAccess(){
        Number userId = insertUser();

        communityJpaDao.updateAccess(userId, COMMUNITY_ID, null);

        assertEquals(Long.valueOf(0), countRowsInAccess());
    }

}