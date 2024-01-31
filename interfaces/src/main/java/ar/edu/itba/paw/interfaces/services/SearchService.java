package ar.edu.itba.paw.interfaces.services;

		import ar.edu.itba.paw.interfaces.exceptions.BadParamsException;
		import ar.edu.itba.paw.models.*;
		import javafx.util.Pair;

		import java.util.List;

public interface SearchService {

	Integer countQuestionQuery(String query , SearchFilter filter , SearchOrder order , Long community , User user );
	List<Question> search(String query , SearchFilter filter , SearchOrder order , Long community ,User user , int limit , int offset);
	List<User> searchUser(String query ,AccessType accessType, Long communityId, String email, int page, int limit) throws BadParamsException;
	Pair<List<Community>,Integer> searchCommunity(String query, Long userId, AccessType accessType, Long moderatorId, int page , int limit) throws BadParamsException;
	Integer searchUserCount(String query , AccessType accessType, Long communityId, String email);
	Integer searchCommunityCount(String query);
	//Te da las preguntas recientes de gente con buen karma
	List<Answer> getTopAnswers(Long userId);
}
