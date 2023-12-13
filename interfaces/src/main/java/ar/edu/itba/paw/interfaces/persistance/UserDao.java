package ar.edu.itba.paw.interfaces.persistance;

import ar.edu.itba.paw.models.AccessType;
import ar.edu.itba.paw.models.Karma;
import ar.edu.itba.paw.models.Notification;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
	List<User> list();

	Optional<User> findByEmail(String email);

	List<User> findByUsername(String username);

	Optional<User> findById(long id);

	User create(String username, String email, String password);

	Optional<User> update(User user, String newUsername, String newPassword);

	Optional<Notification> getNotifications(long userId);

	Optional<Karma> getKarma(long userId);

}
