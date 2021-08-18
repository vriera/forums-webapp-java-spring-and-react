package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.interfaces.persistance.UserDao;
import ar.edu.itba.paw.interfaces.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserDao userDao;

	@Override
	public User findById(long id) {
		return this.userDao.findById(id);
	}

	@Override
	public List<User> list() {
		return this.userDao.list();
	}

	@Override
	public User create(final String username) {
		return userDao.create(username);
	}
}
