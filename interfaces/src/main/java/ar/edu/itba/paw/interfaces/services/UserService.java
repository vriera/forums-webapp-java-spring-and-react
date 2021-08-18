package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.User;

import java.util.List;

public interface UserService {
	User findById(long id);

	List<User> list();

	User create(String username);
}