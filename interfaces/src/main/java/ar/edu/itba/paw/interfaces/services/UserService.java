package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.exceptions.EmailAlreadyExistsException;
import ar.edu.itba.paw.models.exceptions.IncorrectPasswordException;
import ar.edu.itba.paw.models.exceptions.UsernameAlreadyExistsException;

import java.util.List;
import java.util.Optional;

public interface UserService {


	Optional<User> update(User user, String newUsername, String newPassword, String currentPassword) throws UsernameAlreadyExistsException, IncorrectPasswordException;

	Optional<User> findById(long id);

	Optional<User> findByEmail(String email);

	Optional<User> create(String username, String email, String password, String baseUrl) throws UsernameAlreadyExistsException, EmailAlreadyExistsException;

	List<Community> getModeratedCommunities(Number id, Number page);

	long getModeratedCommunitiesPages(Number id);

	List<Community> getCommunitiesByAccessType(Number userId, AccessType type, Number page);

	long getCommunitiesByAccessTypePages(Number userId, AccessType type);

	List<Question> getQuestions(Number id, Number page);

	int getPageAmountForQuestions(Number id);

	List<Answer> getAnswers(Number id, Number page);

	int getPageAmountForAnswers(Number id);

	Optional<Notification> getNotifications(Number userId);

	Optional<Karma> getKarma(Number userId);
}