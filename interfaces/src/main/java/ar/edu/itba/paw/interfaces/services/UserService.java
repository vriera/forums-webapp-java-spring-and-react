package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.exceptions.EmailTakenException;
import ar.edu.itba.paw.models.exceptions.IncorrectPasswordException;
import ar.edu.itba.paw.models.exceptions.UsernameTakenException;

import java.util.List;
import java.util.Optional;

public interface UserService {


	Optional<User> update(User user, String newUsername, String newPassword, String currentPassword) throws UsernameTakenException, IncorrectPasswordException;

	Optional<User> findById(long id);

	List<User> list();

	Optional<User> findByEmail(String email);

	Optional<User> create(String username, String email, String password, String baseUrl) throws UsernameTakenException, EmailTakenException;

	List<Community> getModeratedCommunities(Number id, Number page);

	long getModeratedCommunitiesPages(Number id);

	List<Community> getCommunitiesByAccessType(Number userId, AccessType type, Number page);

	long getCommunitiesByAccessTypePages(Number userId, AccessType type);

	List<Question> getQuestions(Number id, Number page);

	int getPageAmountForQuestions(Number id);

	List<Answer> getAnswers(Number id, Number page);

	int getPageAmountForAnswers(Number id);

	//Recupera las credenciales de acceso del usuario para una comunidad dada
	Optional<AccessType> getAccess(Number userId, Number communityId);

	Optional<Notification> getNotifications(Number userId);


	Optional<Karma> getKarma(Number userId);


	List<User> getUsers(int page);

	boolean isModerator(Number id , Number communityId);
}