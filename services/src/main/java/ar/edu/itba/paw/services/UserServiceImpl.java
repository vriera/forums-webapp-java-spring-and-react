package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.Exceptions.UserAlreadyCreatedException;
import ar.edu.itba.paw.interfaces.persistance.AnswersDao;
import ar.edu.itba.paw.interfaces.persistance.CommunityDao;
import ar.edu.itba.paw.interfaces.persistance.QuestionDao;
import ar.edu.itba.paw.interfaces.persistance.UserDao;
import ar.edu.itba.paw.interfaces.services.MailingService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

	@Autowired
	private MailingService mailingService;

	private final int pageSize = 5;

	@Override
	public Optional<User> updateUser(User user,String currentPassword, String newPassword, String username) {
		String password;
		if(newPassword == null || newPassword.isEmpty()){
			password = null;
		}
		else
			password = encoder.encode(newPassword);
		return userDao.updateCredentials(user, username, password);
	}

	@Override
	public Boolean passwordMatches(String password, User user){
		return encoder.matches(password, user.getPassword());
	}

	@Override
	public Optional<User> findById(long id) {
		if(id < 0 )
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
	public Optional<User> create(final String username, final String email, String password, String baseUrl) throws UserAlreadyCreatedException {
		if ( username == null || username.isEmpty() || findByEmail(username).isPresent() || email == null || email.isEmpty() || password == null || password.isEmpty()){
			return Optional.empty();
		}

		Optional<User> aux = findByEmail(email);

		if(aux.isPresent() ) { //El usuario ya está ingresado, puede ser un guest o alguien repetido
			if (aux.get().getPassword() == null) { //el usuario funcionaba como guest
				return userDao.updateCredentials(aux.get(), username, encoder.encode(password));
			}
			throw new UserAlreadyCreatedException();
		}
		//Solo devuelve un empty si falló la creación en la BD
		return sendEmailUser(Optional.ofNullable(userDao.create(username, email, encoder.encode(password))),baseUrl);
	}

	public Optional<User> sendEmailUser(Optional<User> u, String baseUrl){
		u.ifPresent(user -> mailingService.verifyEmail(user.getEmail(), user,baseUrl, LocaleContextHolder.getLocale()));

		return u;
	}


	@Override

	public List<Community> getModeratedCommunities(Long id, Integer page, Integer limit) {
		if( id < 0 )
			return Collections.emptyList();

		List<Community> cList = communityDao.getByModerator(id, page,limit);
		for (Community c : cList) {
			c = addUserCount(c);
			Optional<CommunityNotifications> notifications = communityDao.getCommunityNotificationsById(c.getId());
			if(notifications.isPresent()) {
				c.setNotifications(notifications.get().getNotifications());
			}else{
				c.setNotifications(0L);
			}
		}
		return cList;
	}

	private Community addUserCount( Community c){
		Number count = communityDao.getUserCount(c.getId()).orElse(0);
		c.setUserCount(count.longValue());
		return c;
	}

	@Override
	public List<Community> getCommunitiesByAccessType(Long userId, AccessType type,Integer page, Integer limit) {
		if( userId < 0 )
			return Collections.emptyList();

		return communityDao.getCommunitiesByAccessType(userId, type,limit, page).stream().map(this::addUserCount).collect(Collectors.toList());
	}

	@Override
	public List<Question> getQuestions(Number id, Number page) {
		if( id.longValue() < 0 )
			return Collections.emptyList();

		return questionDao.findByUser(id.longValue(), page.intValue()*pageSize, pageSize);
	}

	@Override
	public int getPageAmountForQuestions(Number id) {
		if( id.longValue() < 0 )
			return -1;
		int count = questionDao.findByUserCount(id.longValue());
		int mod = (count/pageSize) % pageSize;
		return mod != 0? (count/pageSize)+1 : count/pageSize;
	}

	@Override
	public Optional<Notification> getNotifications(Number userId){
		return userDao.getNotifications(userId);
	}

	@Override
	public Optional<Karma> getKarma(Number userId){return userDao.getKarma(userId);}


}
