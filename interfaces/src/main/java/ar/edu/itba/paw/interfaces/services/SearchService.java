package ar.edu.itba.paw.interfaces.services;

		import ar.edu.itba.paw.models.*;

		import java.util.List;

public interface SearchService {

	long countQuestionQuery(String query , SearchFilter filter , SearchOrder order , Number community , User user );
	List<Question> search(String query , SearchFilter filter , SearchOrder order , Number community ,User user , int limit , int offset);
	List<User> searchUser(String query , int limit , int offset , String email);
	List<Community> searchCommunity(String query, int limit , int offset);
	long searchUserCount(String query , String email);
	long searchCommunityCount(String query);
	//Te da las preguntas recientes de gente con buen karma

	List<Community> searchCommunity(String query, AccessType accessType , Integer moderatorId , Integer userId, int limit , int offset);
	long searchCommunityCount(String query , AccessType accessType , Integer moderatorId , Integer userId );

	List<Answer> getTopAnswers(Number userId);
}
