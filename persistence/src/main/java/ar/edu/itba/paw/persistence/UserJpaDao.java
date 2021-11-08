package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistance.UserDao;
import ar.edu.itba.paw.models.AccessType;
import ar.edu.itba.paw.models.Karma;
import ar.edu.itba.paw.models.Notification;
import ar.edu.itba.paw.models.User;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

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
		LOGGER.debug("Usuario creado: {} => {} con id {}", user.getUsername(), user.getEmail(), user.getId());
		return user;
	}

	@Override
	public Optional<User> updateCredentials(Number id, String newUsername, String newPassword) {
		final Query query = em.createQuery("update User as u set u.username = :username, u.password = :password where u.id = :id");
		query.setParameter("username", newUsername);
		query.setParameter("password", newPassword);
		query.setParameter("id", id.longValue());
		int resultId = query.executeUpdate();
		return findById(resultId);
	}

	@Override
	public List<User> getMembersByAccessType(Number communityId, AccessType type, long offset, long limit) {
		String queryString = "select a.user from Access as a where a.community.id = :communityId";
		if(type != null)
			queryString = queryString+" and a.accessType = :accessType";

		final TypedQuery<User> query = em.createQuery(queryString, User.class);
		query.setParameter("communityId", communityId);
		query.setParameter("accessType", type);
		query.setFirstResult((int) offset);
		query.setMaxResults((int) limit);
		return query.getResultList();
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
	public Optional<Notification> getNotifications(Number userId) { //TODO: falta implementar
		TypedQuery<Notification> query = em.createQuery("select n from Notification n where n.user.id = :userId", Notification.class);
		query.setParameter("userId", userId.longValue());
		return query.getResultList().stream().findFirst();
	}

	@Override
	public Optional<Karma> getKarma(Number userId){
		TypedQuery<Karma> query = em.createQuery("select k from Karma k where k.user.id = :user_id" , Karma.class);
		query.setParameter("user_id" , userId.longValue());
		return query.getResultList().stream().findFirst();
	};
}
