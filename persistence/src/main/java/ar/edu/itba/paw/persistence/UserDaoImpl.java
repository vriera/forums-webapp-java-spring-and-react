package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.interfaces.persistance.UserDao;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class UserDaoImpl implements UserDao {
	// Mock of a db. Just for show :)
	private Map<String, User> users = new ConcurrentHashMap<>();

	public UserDaoImpl() {
		User user = new User();
		user.setId("1");
		user.setName("Leo");
		user.setPassword("1234");
		users.put("1", user);
		User user2 = new User();
		user2.setId("2");
		user2.setName("Alvaro");
		user2.setPassword("12345");
		users.put("2", user2);
		User user3 = new User();
		user3.setId("3");
		user3.setName("Salungo");
		user3.setPassword("malungo!");
		users.put("3", user3);
	}

	@Override
	public User get(String id) {
		return users.get(id);
	}

	@Override
	public List<User> list() {
		return new ArrayList<>(users.values());
	}

	@Override
	public User save(User user) {
		return users.put(user.getId(), user);
	}

}
