package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.services.MailingService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.interfaces.persistance.UserDao;
import ar.edu.itba.paw.interfaces.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserDao userDao;

	@Autowired
	private MailingService mailingService;

	@Autowired
	private PasswordEncoder encoder;

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
	public Optional<User> findByEmail(String email) {
		if(email.isEmpty())
			return Optional.empty();

		return userDao.findByEmail(email);
	}

	@Override
	public Optional<User> create(final String username, final String email, String password) {
		if ( username == null || username.isEmpty() || findByEmail(username).isPresent() || email == null || email.isEmpty() || password == null || password.isEmpty()){
			return Optional.empty();
		}

		if(findByEmail(email).isPresent())
			return Optional.empty();

		//Solo devuelve un empty si falló la creación en la BD
		Optional<User> u =  Optional.ofNullable(userDao.create(username, email, encoder.encode(password)));

		u.ifPresent(user -> mailingService.sendMail(user.getEmail(),
				"Registro exitoso", "Enhorabuena " + user.getUsername() + "!\nTu usuario fue creado con éxito en AskAway"));
		return u;
	}
}
