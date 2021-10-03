package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Answer;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

	Optional<User> findById(long id);

	List<User> list();
	Optional<User> verify(Long id);

	Optional<User> findByEmail(String email);

	Optional<User> create(String username, String email, String password );

	List<Community> getModeratedCommunities(long id, Number page);

	List<Question> getQuestions(long id, Number page);

	List<Answer> getAnswers(long id, Number page);
}