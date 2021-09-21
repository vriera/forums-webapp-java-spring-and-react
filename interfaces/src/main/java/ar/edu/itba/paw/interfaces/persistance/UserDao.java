package ar.edu.itba.paw.interfaces.persistance;

import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
	List<User> list();

	Optional<User> findByEmail(String email);

	Optional<User> findById(long id);

	User create(String username, String email, String password );

	Optional<User> updateCredentials(Number id, String newUsername, String newPassword);
}
