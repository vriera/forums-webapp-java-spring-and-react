package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.*;

import java.util.List;
import java.util.Optional;

public interface SearchService {

	Integer countQuestionQuery(String query , SearchFilter filter , SearchOrder order , Number community , User user );
	List<Question> search(String query , SearchFilter filter , SearchOrder order , Number community ,User user , int limit , int offset);
	List<User> searchUser(String query);
	List<Community> searchCommunity(String query);
	//Te da las preguntas recientes de gente con buen karma
	List<Answer> getTopAnswers(Number userId);
}
