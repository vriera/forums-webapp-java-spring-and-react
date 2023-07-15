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

	User create(String username, String email, String password );

	Optional<User> update(User user, String newUsername, String newPassword);

	// Returns the users with access to the community given an access type
	List<User> getMembersByAccessType(Number communityId, AccessType type, long offset, long limit);

	// Returns the amount of pages needed to display the data
	long getMemberByAccessTypeCount(Number communityId, AccessType type);

	Optional<Notification> getNotifications(Number userId);

	Optional<Karma> getKarma(Number userId);

    List<User> getUsers(int page);
}
