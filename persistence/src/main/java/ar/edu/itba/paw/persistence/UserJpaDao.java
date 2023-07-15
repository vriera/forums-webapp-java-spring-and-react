package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistance.UserDao;
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
		final User user = new User(null,username,email,password);
		em.persist(user);

		LOGGER.debug("Created new user: username: {}, email: {}, id: {}", user.getUsername(), user.getEmail(), user.getId());
		return user;
	}


	@Override
	@Transactional
	public Optional<User> update(User user, String newUsername, String newPassword) {
		final Query query = em.createQuery("update User as u set u.username = :newUsername, u.password = :newPassword where u.id = :id");
		query.setParameter("newPassword", newPassword);
		query.setParameter("newUsername", newUsername);
		query.setParameter("id", user.getId());
		query.executeUpdate();

		return this.findById(user.getId());
	}

	@Override
	public List<User> findByUsername(String username) {
		final TypedQuery<User> query = em.createQuery("select u from User u where u.username = :username", User.class);
		query.setParameter("username", username);
		return query.getResultList();
	}

	@Override
	public List<User> getMembersByAccessType(Number communityId, AccessType type, long offset, long limit) {

		String select = "SELECT access.user_id from access where access.community_id = :id";
		if(type != null)
			select+= " and access.access_type = :type";

		Query nativeQuery = em.createNativeQuery(select);
		nativeQuery.setParameter("id", communityId);
		nativeQuery.setFirstResult((int)offset);
		nativeQuery.setMaxResults((int)limit);

		if(type != null)
			nativeQuery.setParameter("type", type.ordinal());

		@SuppressWarnings("unchecked")
		final List<Integer> userIds = (List<Integer>) nativeQuery.getResultList();

		if(userIds.isEmpty()){
			return Collections.emptyList();
		}

		final TypedQuery<User> query = em.createQuery("from User where id IN :userIds", User.class);
		query.setParameter("userIds", userIds.stream().map(Long::new).collect(Collectors.toList()));

		List<User> list = query.getResultList().stream().collect(Collectors.toList());
		return list;
	}

	@Override
	public long getMemberByAccessTypeCount(Number communityId, AccessType type) {
		String queryString = "select count(a.id) from Access as a where a.community.id = :communityId";
		if(type != null)
			queryString = queryString+" and a.accessType = :accessType";

		final Query query = em.createQuery(queryString);
		query.setParameter("communityId", communityId.longValue());
		query.setParameter("accessType", type);
		return (Long) query.getSingleResult();
	}

	@Override
	public Optional<Notification> getNotifications(Number userId) {
		TypedQuery<Notification> query = em.createQuery("select n from Notification n where n.user.id = :userId", Notification.class);
		query.setParameter("userId", userId.longValue());
		return query.getResultList().stream().findFirst();
	}

	@Override
	public Optional<Karma> getKarma(Number userId){
		TypedQuery<Karma> query = em.createQuery("select k from Karma k where k.user.id = :user_id" , Karma.class);
		query.setParameter("user_id" , userId.longValue());
		return query.getResultList().stream().findFirst();
	}

	@Override
	public List<User> getUsers(int page) {

		final Query query = em.createNativeQuery("select user_id from users LIMIT 10 OFFSET :OFFSET ");
		query.setParameter("OFFSET",10*(page-1));
		@SuppressWarnings("unchecked")
		List<Integer> userIds = query.getResultList();
		if (userIds.isEmpty()) return Collections.emptyList();
		final TypedQuery<User> q = em.createQuery("from User where id IN :userIds", User.class);
		q.setParameter("userIds", userIds.stream().map(Long::new).collect(Collectors.toList()));
		return q.getResultList().stream().collect(Collectors.toList());
	}
}
