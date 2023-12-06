package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.exceptions.EmailAlreadyExistsException;
import ar.edu.itba.paw.models.exceptions.IncorrectPasswordException;
import ar.edu.itba.paw.models.exceptions.UsernameAlreadyExistsException;

import java.util.List;

public interface UserService {


	User update(User user, String newUsername, String newPassword, String currentPassword);

	User findById(long id);

	List<User> list();

	User findByEmail(String email);

	User create(String username, String email, String password, String baseUrl);

	List<Community> getModeratedCommunities(Number id, Number page);

	long getModeratedCommunitiesPagesCount(Number id);

	List<Community> getCommunitiesByAccessType(Number userId, AccessType type, Number page);

	long getCommunitiesByAccessTypePagesCount(Number userId, AccessType type);

	List<Question> getQuestions(Number id, Number page);

	long getQuestionsPagesCount(Number id);

	List<Answer> getAnswers(Long id, int page);

	long getAnswersPagesCount(Long id);

	//Recupera las credenciales de acceso del usuario para una comunidad dada
	AccessType getAccess(Number userId, Number communityId);

	Notification getNotifications(Number userId);


	Karma getKarma(Number userId);

//
//	List<User> getUsers(int page);
//
//	boolean isModerator(Number id , Number communityId);

}