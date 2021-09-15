package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.interfaces.persistance.UserDao;
import ar.edu.itba.paw.interfaces.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserDao userDao;

	@Override
	public Optional<User> findById(long id) {
		if(id <= 0 )
			return Optional.empty();

		return userDao.findById(id);
	}

	@Override
	public List<User> list() {
		return this.userDao.list();
	}

	@Override
	public Optional<User> verify(Long id) {
		return Optional.empty(); //llenar esto para verificar el mail
	}

	@Override
	public Optional<User> findByEmail(String email) {
		if(email.isEmpty())
			return Optional.empty();

		return userDao.findByEmail(email);
	}

	@Override
	public Optional<User> create(final String username, final String email) {
		if ( username == null || username.isEmpty() || findByEmail(username).isPresent() || email == null || email.isEmpty()){
			return Optional.empty(); //Solo devuelve un empty si no se creó nada en la BD
		}
		return Optional.ofNullable(userDao.create(username, email));
	}
}
