package ar.edu.itba.paw.interfaces.services;

		import ar.edu.itba.paw.models.*;

		import java.util.List;

public interface SearchService {

	long searchQuestionPagesCount(String query , SearchFilter filter , SearchOrder order , Number community , User user );
	List<Question> search(String query , SearchFilter filter , SearchOrder order , Number community ,User user , int page);
	List<User> searchUser(String query , String email , int page);
	List<Community> searchCommunity(String query, int page);
	long searchUserCount(String query , String email);
	long searchCommunityPagesCount(String query);
	//Te da las preguntas recientes de gente con buen karma

	List<Community> searchCommunity(String query, AccessType accessType , Integer moderatorId , Integer userId, int page);
	long searchCommunityPagesCount(String query , AccessType accessType , Integer moderatorId , Integer userId );

	List<Answer> getTopAnswers(Number userId);
}
