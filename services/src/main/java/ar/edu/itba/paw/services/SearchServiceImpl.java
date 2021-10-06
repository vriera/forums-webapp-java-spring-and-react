package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistance.QuestionDao;
import ar.edu.itba.paw.interfaces.persistance.SearchDao;
import ar.edu.itba.paw.interfaces.services.QuestionService;
import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.models.Answer;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {
	@Autowired
	SearchDao searchDao;

	@Autowired
	QuestionService questionService;
	@Override
	public List<User> searchUser(String query){
		if (query.length() == 0 ){
			return Collections.emptyList();
		}
		return  searchDao.searchUser(query);
	}
	@Override
	public List<Community> searchCommunity(String query){
		if (query.length() == 0 ){
			return Collections.emptyList();
		}
		return searchDao.searchCommunity(query);
	};
	@Override
	public List<Question> search(String query , Number filter , Number order , Number community) {
		if(query == null || query.isEmpty())
			return searchDao.search(filter , order , community);
		return searchDao.search(query , filter,  order, community);
	}

	@Override
	public List<Answer> getTopAnswers(){
		return searchDao.getTopAnswers();
	}
}
