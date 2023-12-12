package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.*;
import java.util.List;

public interface UserService {

	User update(User user, String newUsername, String newPassword, String currentPassword);

	User findById(long id);

	User findByEmail(String email);

	User create(String username, String email, String password, String baseUrl);

	List<Community> getCommunitiesByAccessType(Number userId, AccessType type, Number page);

	long getCommunitiesByAccessTypePagesCount(Number userId, AccessType type);

	List<Question> getQuestions(Number id, Number page);

	long getQuestionsPagesCount(Number id);

	List<Answer> getAnswers(Long id, int page);

	long getAnswersPagesCount(Long id);

	Notification getNotifications(Number userId);

	Karma getKarma(Number userId);

}