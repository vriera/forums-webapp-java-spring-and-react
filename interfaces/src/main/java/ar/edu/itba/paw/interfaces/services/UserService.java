package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

	Optional<User> findById(long id);

	List<User> list();

	Optional<User> findByEmail(String email);

	Optional<User> create(String username, String email );
}