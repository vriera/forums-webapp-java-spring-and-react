package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Answer;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;

import java.util.List;

public interface SearchService {

	Integer countQuestionQuery(String query , Number filter , Number order , Number community , User user );
	List<Question> search(String query , Number filter , Number order , Number community ,User user , int limit , int offset);
	List<User> searchUser(String query);
	List<Community> searchCommunity(String query);
	//Te da las preguntas recientes de gente con buen karma
	List<Answer> getTopAnswers();
}
