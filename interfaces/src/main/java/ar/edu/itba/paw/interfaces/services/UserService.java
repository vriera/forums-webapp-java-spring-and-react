package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.*;

import java.util.List;
import java.util.Optional;

public interface UserService {

	Optional<User> findById(long id);

	List<User> list();
	Optional<User> verify(Long id);

	Optional<User> findByEmail(String email);

	Optional<User> create(String username, String email, String password );

	List<Community> getModeratedCommunities(Number id, Number page);

	List<Community> getCommunitiesByAccessType(Number userId, AccessType type, Number page);

	List<Question> getQuestions(Number id, Number page);

	int getPageAmmountForQuestions(Number id);

	List<Answer> getAnswers(Number id, Number page);

	int getPageAmmountForAnswers(Number id);
}