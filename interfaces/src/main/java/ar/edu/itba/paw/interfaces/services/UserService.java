package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.*;

import java.util.List;
import java.util.Optional;

public interface UserService {


	Optional<User> updateUser(User user, String currentPassword, String newPassword, String username);

	Boolean passwordMatches(String password, User user);

	Optional<User> findById(long id);

	List<User> list();

	Optional<User> verify(Long id);

	Optional<User> findByEmail(String email);

	Optional<User> create(String username, String email, String password, String baseUrl);

	List<Community> getModeratedCommunities(Long userId, Integer page, Integer limit);


	List<Community> getCommunitiesByAccessType(Long userId, AccessType type, Integer page, Integer limit);

	long getCommunitiesByAccessTypePages(Number userId, AccessType type);

	List<Question> getQuestions(Number id, Number page);

	int getPageAmountForQuestions(Number id);

	List<Answer> getAnswers(Number id, Number page);

	int getPageAmountForAnswers(Number id);

	Optional<Notification> getNotifications(Number userId);


	Optional<Karma> getKarma(Number userId);


	List<User> getUsers(int page);

}