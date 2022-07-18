package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistance.AnswersDao;
import ar.edu.itba.paw.interfaces.persistance.CommunityDao;
import ar.edu.itba.paw.interfaces.persistance.QuestionDao;
import ar.edu.itba.paw.interfaces.persistance.UserDao;
import ar.edu.itba.paw.interfaces.services.MailingService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
	public Optional<User> create(final String username, final String email, String password) {
		if ( username == null || username.isEmpty() || findByEmail(username).isPresent() || email == null || email.isEmpty() || password == null || password.isEmpty()){
			return Optional.empty();
		}

		Optional<User> aux = findByEmail(email);

		if(aux.isPresent() ) { //El usuario ya está ingresado, puede ser un guest o alguien repetido
			if (aux.get().getPassword() == null) { //el usuario funcionaba como guest
				return userDao.updateCredentials(aux.get(), username, encoder.encode(password));
			}
			return Optional.empty();
		}
		//Solo devuelve un empty si falló la creación en la BD
		return sendEmailUser(Optional.ofNullable(userDao.create(username, email, encoder.encode(password))));
	}

	public Optional<User> sendEmailUser(Optional<User> u){
		u.ifPresent(user -> mailingService.verifyEmail(user.getEmail(), user, ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString()));

		return u;
	}

	@Override
	public List<Community> getModeratedCommunities(Number id, Number page) {
		if( id.longValue() < 0 || page.intValue() < 0)
			return Collections.emptyList();

		List<Community> cList = communityDao.getByModerator(id, page.intValue()*pageSize, pageSize);
		for (Community c : cList) {
			Optional<CommunityNotifications> notifications = communityDao.getCommunityNotificationsById(c.getId());
			if(notifications.isPresent()) {
				c.setNotifications(notifications.get().getNotifications());
			}else{
				c.setNotifications(0L);
			}
		}
		return cList;
	}


	@Override
	public long getModeratedCommunitiesPages(Number id) {
		if(id == null || id.longValue() < 0)
			return -1;

		long total = communityDao.getByModeratorCount(id);
		return (total%pageSize == 0)? total/pageSize : (total/pageSize)+1;
	}

	@Override
	public boolean isModerator(Number id , Number communityId) {
		long pages = getModeratedCommunitiesPages(id);
		for (int i = 1; i <= pages; i++) {
			List<Community> cl = getModeratedCommunities(id, i);
			for (Community c : cl) {
				if (c.getId() == communityId.longValue()) {
					return true;
				}
			}
		}
		return false;
	}
	@Override
	public List<Community> getCommunitiesByAccessType(Number userId, AccessType type, Number page) {
		if( userId.longValue() < 0 )
			return Collections.emptyList();

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
	public List<Answer> getAnswers(Number id, Number page) {
		if( id.longValue() < 0 )
			return Collections.emptyList();

		return answersDao.findByUser(id.longValue(), page.intValue()*pageSize, pageSize);
	}

	@Override
	public int getPageAmountForAnswers(Number id) {
		if(id.longValue() < 0){
			return -1;
		}
		int count = answersDao.findByUserCount(id.longValue()).get().intValue(); // deberiamos preguntar si existe?
		int mod = (count/pageSize)% pageSize;

		return mod != 0? (count/pageSize)+1 : count/pageSize;
	}

	@Override
	public Optional<AccessType> getAccess(Number userId, Number communityId) {
		if(userId == null || userId.longValue() < 0 || communityId == null || communityId.longValue() < 0)
			return Optional.empty();
		return communityDao.getAccess(userId, communityId);
	}
	@Override
	public Optional<Notification> getNotifications(Number userId){
		return userDao.getNotifications(userId);
	}

	@Override
	public Optional<Karma> getKarma(Number userId){return userDao.getKarma(userId);}

	@Override
	public List<User> getUsers(int page) {
		return userDao.getUsers(page);
	}

}
