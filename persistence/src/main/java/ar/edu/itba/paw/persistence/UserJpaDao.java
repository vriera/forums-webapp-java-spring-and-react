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
	public Optional<Notification> getNotifications(long userId) {
		TypedQuery<Notification> query = em.createQuery("select n from Notification n where n.user.id = :userId", Notification.class);
		query.setParameter("userId", userId);
		return query.getResultList().stream().findFirst();
	}

	@Override
	public Optional<Karma> getKarma(long userId){
		TypedQuery<Karma> query = em.createQuery("select k from Karma k where k.user.id = :user_id" , Karma.class);
		query.setParameter("user_id" , userId);
		return query.getResultList().stream().findFirst();
	}

}
