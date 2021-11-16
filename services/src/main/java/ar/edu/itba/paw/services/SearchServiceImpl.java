package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistance.SearchDao;
import ar.edu.itba.paw.interfaces.services.QuestionService;
import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.models.Answer;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {
	@Autowired
	SearchDao searchDao;

	@Autowired
	QuestionService questionService;

	@Override
	public List<Question> search(String query , Number filter , Number order , Number community , User user , int limit , int offset) {
		if( user == null){
			user = new User(-1L , "", "" , "");
		}
		if(query == null || query.isEmpty())
			return searchDao.search(filter , order , community , user , limit , offset);
		return searchDao.search(query , filter,  order, community , user , limit , offset);
	}

	@Override
	public List<Answer> getTopAnswers(){
		return searchDao.getTopAnswers();
	}

	@Override
	public Integer countQuestionQuery(String query , Number filter , Number order , Number community , User user ) {
		if( user == null){
			user = new User(-1L , "", "" , "");
		}
		if(query == null || query.isEmpty())
			return searchDao.search(filter , order , community , user , -1, -1).size();
		return searchDao.search(query , filter,  order, community , user , -1 , -1).size();
	}

	@Override
	public List<Community> searchCommunity(String query) {
		return searchDao.searchCommunity(query);
	}
	@Override
	public List<User> searchUser(String query){
		return searchDao.searchUser(query);
	}
}
