package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistance.SearchDao;
import ar.edu.itba.paw.interfaces.services.CommunityService;
import ar.edu.itba.paw.interfaces.services.QuestionService;
import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {
	@Autowired
	SearchDao searchDao;
	@Autowired
	CommunityService communityService;
	@Autowired
	QuestionService questionService;

	@Override
	public List<Question> search(String query , SearchFilter filter , SearchOrder order , Number community , User user , int limit , int offset) {
		if( user == null){
			user = new User(-1L , "", "" , "");
		}
		if(query == null || query.isEmpty())
			return searchDao.search(filter , order , community , user , limit , offset);
		return searchDao.search(query , filter,  order, community , user , limit , offset);
	}

	@Override
	public List<Answer> getTopAnswers(Number userId){
		return searchDao.getTopAnswers(userId);
	}

	@Override
	public Integer countQuestionQuery(String query , SearchFilter filter , SearchOrder order , Number community , User user ) {
		if( user == null){
			user = new User(-1L , "", "" , "");
		}
		if(query == null || query.isEmpty()) {
			return searchDao.searchCount(filter , community , user).intValue();
		}
		return searchDao.searchCount(query ,filter , community ,user).intValue();
	}

	@Override
	public List<Community> searchCommunity(String query , int limit , int offset) {
		 List<Community> communities = searchDao.searchCommunity(query , limit , offset );
		for (Community c : communities) {
			c.setUserCount(communityService.getUserCount(c.getId()).orElse(0).longValue() + 1);
		}
		return communities;
	}

	@Override
	public List<User> searchUser(String query , int limit , int offset){

		return searchDao.searchUser(query , limit , offset);
	}
	@Override
	public Integer searchUserCount(String query){
		return searchDao.searchUser( query , -1 , -1 ).size();
	}

	@Override
	public Integer searchCommunityCount(String query){
		return searchDao.searchCommunity( query , -1 , -1 ).size();
	}
}
