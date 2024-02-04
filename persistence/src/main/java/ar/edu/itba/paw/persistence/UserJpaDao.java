package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistance.UserDao;
import ar.edu.itba.paw.models.AccessType;
import ar.edu.itba.paw.models.Karma;
import ar.edu.itba.paw.models.Notification;
import ar.edu.itba.paw.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Primary
@Repository
public class UserJpaDao implements UserDao {

    @PersistenceContext
    private EntityManager em;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserJpaDao.class);

    @Override
    public List<User> list() {
        return em.createQuery("select u from User u", User.class).getResultList();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        final TypedQuery<User> query = em.createQuery("select u from User u where u.email = :email", User.class);
        query.setParameter("email", email);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public Optional<User> findById(long id) {
        return Optional.ofNullable(em.find(User.class, id));
    }

    @Override
    @Transactional
    public User create(String username, String email, String password) {
        final User user = new User(null, username, email, password);
        em.persist(user);
        LOGGER.debug("Usuario creado: {} => {} con id {}", user.getUsername(), user.getEmail(), user.getId());
        return user;
    }


    @Override
    @Transactional
    public Optional<User> updateCredentials(User user, String newUsername, String newPassword) {
        final Query query;

        if (newPassword == null || newPassword.isEmpty()) {
            query = em.createQuery("update User as u set u.username = :username where u.id = :id");
            LOGGER.debug("Entre al update de username SIN password");
        } else {
            query = em.createQuery("update User as u set u.username = :username, u.password = :password where u.id = :id");
            query.setParameter("password", newPassword);
            LOGGER.debug("Entre al update con user y contraseña");
        }
        query.setParameter("username", newUsername);
        query.setParameter("id", user.getId());
        int resultId = query.executeUpdate();
        return findById(resultId);
    }

    @Override
    public List<User> getMembersByAccessType(Number communityId, AccessType type, int page, int limit) {

        String select = "SELECT access.user_id from access where access.community_id = :id";
        if (type != null)
            select += " and access.access_type = :type";

        Query nativeQuery = em.createNativeQuery(select);
        nativeQuery.setParameter("id", communityId);
        if (limit > 0 && page > 0) {
            nativeQuery.setFirstResult(limit * (page - 1)); //offset
            nativeQuery.setMaxResults(limit);
        }

        if (type != null)
            nativeQuery.setParameter("type", type.ordinal());

        @SuppressWarnings("unchecked") final List<Integer> userIds = (List<Integer>) nativeQuery.getResultList();// .stream().map(e -> Integer.valueOf(e.toString())).collect(Collectors.toList());

        if (userIds.isEmpty()) {
            return Collections.emptyList();
        }

        final TypedQuery<User> query = em.createQuery("from User where id IN :userIds", User.class);
        query.setParameter("userIds", userIds.stream().map(Long::new).collect(Collectors.toList()));

        return query.getResultList().stream().collect(Collectors.toList());

        /*
        String queryString = "select a.user from Access as a where a.community.id = :communityId";
        if(type != null)
            queryString = queryString+" and a.accessType = :accessType";

        final TypedQuery<User> query = em.createQuery(queryString, User.class);
        query.setParameter("communityId", communityId);
        query.setParameter("accessType", type);
        query.setFirstResult((int) offset);
        query.setMaxResults((int) limit);
        return query.getResultList();

         */
    }

    @Override
    public long getMemberByAccessTypeCount(Long communityId, AccessType type) {
        String queryString = "select count(a.id) from Access as a where a.community.id = :communityId";
        if (type != null)
            queryString = queryString + " and a.accessType = :accessType";

        final Query query = em.createQuery(queryString);
        query.setParameter("communityId", communityId);
        query.setParameter("accessType", type);
        return (Long) query.getSingleResult();
    }

    @Override
    public Optional<Notification> getNotifications(Number userId) { //TODO: falta implementar
        TypedQuery<Notification> query = em.createQuery("select n from Notification n where n.user.id = :userId", Notification.class);
        query.setParameter("userId", userId.longValue());
        return query.getResultList().stream().findFirst();
    }

    @Override
    public Optional<Karma> getKarma(Number userId) {
        TypedQuery<Karma> query = em.createQuery("select k from Karma k where k.user.id = :user_id", Karma.class);
        query.setParameter("user_id", userId.longValue());
        return query.getResultList().stream().findFirst();
    }

    @Override
    public List<User> getUsers(int page) {

        final Query query = em.createNativeQuery("select user_id from users LIMIT 10 OFFSET :OFFSET ");
        query.setParameter("OFFSET", 10 * (page - 1));
        @SuppressWarnings("unchecked")
        List<Integer> userIds = (List<Integer>) query.getResultList();
        if (userIds.isEmpty()) return Collections.emptyList();
        final TypedQuery<User> q = em.createQuery("from User where id IN :userIds", User.class);
        q.setParameter("userIds", userIds.stream().map(Long::new).collect(Collectors.toList()));
        return q.getResultList().stream().collect(Collectors.toList());


    }
}
