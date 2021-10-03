package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.services.MailingService;
import ar.edu.itba.paw.models.Answer;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.interfaces.persistance.UserDao;
import ar.edu.itba.paw.interfaces.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserDao userDao;

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	MailingService mailingService;

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
	@Transactional
	public Optional<User> create(final String username, final String email, String password) {
		if ( username == null || username.isEmpty() || findByEmail(username).isPresent() || email == null || email.isEmpty() || password == null || password.isEmpty()){
			return Optional.empty();
		}

		Optional<User> aux = findByEmail(email);

		if(aux.isPresent() ) { //El usuario ya está ingresado, puede ser un guest o alguien repetido
			if (aux.get().getPassword() == null) { //el usuario funcionaba como guest
				return userDao.updateCredentials(aux.get().getId(), aux.get().getUsername(), encoder.encode(password));
			}
			return Optional.empty();
		}
		//Solo devuelve un empty si falló la creación en la BD
		return sendEmailUser(Optional.ofNullable(userDao.create(username, email, encoder.encode(password))));
	}

	public Optional<User> sendEmailUser(Optional<User> u){
		System.out.println(u.get().getEmail());
		u.ifPresent(user -> mailingService.verifyEmail(user.getEmail(), user));

		return u;
	}
}
