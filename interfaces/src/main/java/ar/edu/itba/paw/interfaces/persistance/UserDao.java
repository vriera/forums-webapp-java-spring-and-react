package ar.edu.itba.paw.interfaces.persistance;

import ar.edu.itba.paw.models.User;

import java.util.List;

public interface UserDao {
    User findById(long id);

	List<User> list();

	User create(String username);
}
