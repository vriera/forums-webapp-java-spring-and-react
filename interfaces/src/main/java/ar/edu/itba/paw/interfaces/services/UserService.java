package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.*;
import java.util.List;

public interface UserService {

	User update(User user, String newUsername, String newPassword, String currentPassword);

	User findById(long id);

	User findByEmail(String email);

	User create(String username, String email, String password, String baseUrl);

	List<Community> getCommunitiesByAccessType(long userId, AccessType type, int page);

	long getCommunitiesByAccessTypePagesCount(long userId, AccessType type);

	List<Question> getQuestions(long userId, int page);

	long getQuestionsPagesCount(long userId);

	List<Answer> getAnswers(long userId, int page);

	long getAnswersPagesCount(long userId);

	Notification getNotifications(long userId);

	Karma getKarma(long userId);

}