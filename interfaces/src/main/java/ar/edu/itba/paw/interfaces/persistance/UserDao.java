package ar.edu.itba.paw.interfaces.persistance;

import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    Optional<User> findById(long id);

	List<User> list();

	Optional<User> findByUsername(String username);

	Optional<User> findByEmail(String email);

	User create(String username, String email );
}
