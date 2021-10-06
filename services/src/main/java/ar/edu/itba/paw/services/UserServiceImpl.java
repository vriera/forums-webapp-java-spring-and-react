package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistance.AnswersDao;
import ar.edu.itba.paw.interfaces.persistance.CommunityDao;
import ar.edu.itba.paw.interfaces.persistance.QuestionDao;
import ar.edu.itba.paw.interfaces.services.MailingService;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.interfaces.persistance.UserDao;
import ar.edu.itba.paw.interfaces.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserDao userDao;

	@Autowired
	private CommunityDao communityDao;

	@Autowired
	private QuestionDao questionDao;

	@Autowired
	private AnswersDao answersDao;

	@Autowired
	private PasswordEncoder encoder;

	private final int pageSize = 10;

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
		return Optional.ofNullable(userDao.create(username, email, encoder.encode(password)));
	}

	@Override
	public List<Community> getModeratedCommunities(Number id, Number page) {
		if( id.longValue() < 0 )
			return Collections.emptyList();
		int pageSize = 10;
		return communityDao.getByModerator(id, page.intValue()*pageSize, pageSize);
	}

	@Override
	public long getModeratedCommunitiesPages(Number id) {
		if(id == null || id.longValue() < 0)
			return -1;

		long total = communityDao.getByModeratorCount(id);
		return (total%pageSize == 0)? total/pageSize : (total/pageSize)+1;
	}

	@Override
	public List<Community> getCommunitiesByAccessType(Number userId, AccessType type, Number page) {
		if( userId.longValue() < 0 )
			return Collections.emptyList();

		int pageSize = 10;
		return communityDao.getCommunitiesByAccessType(userId, type,page.longValue()*pageSize, pageSize);
	}

	@Override
	public long getCommunitiesByAccessTypePages(Number userId, AccessType type) {
		if(userId == null || userId.longValue() < 0)
			return -1;

		long total = communityDao.getCommunitiesByAccessTypeCount(userId, type);
		return (total%pageSize == 0)? total/pageSize : (total/pageSize)+1;
	}

	@Override
	public List<Question> getQuestions(Number id, Number page) {
		if( id.longValue() < 0 )
			return Collections.emptyList();

		int pageSize = 10;
		return questionDao.findByUser(id.longValue(), page.intValue()*pageSize, pageSize);
	}

	@Override
	public List<Answer> getAnswers(Number id, Number page) {
		if( id.longValue() < 0 )
			return Collections.emptyList();

		int pageSize = 10;
		return answersDao.findByUser(id.longValue(), page.intValue()*pageSize, pageSize);
	}
}
