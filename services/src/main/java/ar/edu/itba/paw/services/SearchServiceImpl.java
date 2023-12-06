package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistance.QuestionDao;
import ar.edu.itba.paw.interfaces.persistance.SearchDao;
import ar.edu.itba.paw.interfaces.services.CommunityService;
import ar.edu.itba.paw.interfaces.services.QuestionService;
import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.exceptions.IllegalCommunitySearchArgumentsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

@Service
public class SearchServiceImpl implements SearchService {
	private static final int PAGE_SIZE = 10;
	@Autowired
	private SearchDao searchDao;

	@Autowired
	private UserService userService;

	@Autowired
	private CommunityService communityService;

	@Autowired
	private QuestionDao questionDao;
	@Override
	public List<Question> search(String query , SearchFilter filter , SearchOrder order , Number community , User user , int limit , int offset) {
		if( user == null){
			user = new User(-1L , "", "" , "");
		}
		List<Question> q;
		if(query == null || query.isEmpty())
			q= searchDao.search(filter , order , community , user , limit , offset);
		q= searchDao.search(query , filter,  order, community , user , limit , offset);
		q.forEach( x -> x.setVotes((int)questionDao.getTotalVotesByQuestionId(x.getId())));
		return q;
	}

	@Override
	public List<Answer> getTopAnswers(Number userId){
		return searchDao.getTopAnswers(userId);
	}

	@Override
	public long countQuestionQuery(String query , SearchFilter filter , SearchOrder order , Number community , User user ) {
		if( user == null){
			user = new User(-1L , "", "" , "");
		}
		if(query == null || query.isEmpty()) {
			return searchDao.searchCount(filter , community , user);
		}
		return searchDao.searchCount(query ,filter , community ,user);
	}

	@Override
	public List<Community> searchCommunity(String query , int limit , int offset) {
		List<Community> communities = searchDao.searchCommunity(query , limit , offset );
		for (Community c : communities) {
			//Puede ser que el numero de las comunidades haya que subirlo uno siepre
			c.setUserCount(communityService.getUserCount(c.getId()));
		}
		return communities;
	}
	@Override
	public List<Community> searchCommunity(String query, AccessType accessType , Integer moderatorId , Integer userId , int limit , int offset){
		boolean hasQuery = query != null && !query.isEmpty();
		boolean hasAT = accessType != null;
		boolean hasModeratorId = moderatorId != null;
		boolean hasUserId = userId != null;
		Boolean[] conditions = {hasQuery , hasAT , hasModeratorId};

		long count = Arrays.stream(conditions).filter(b->b).count();

		if(count > 1 || (hasUserId && !hasAT))
			throw new IllegalCommunitySearchArgumentsException();

		if(hasQuery || count == 0)
			return this.searchCommunity(query , limit , offset);
		if(hasAT)
			return userService.getCommunitiesByAccessType(userId , accessType, Math.floor((double)offset/limit));

		return communityService.getByModerator(moderatorId , offset , limit);
	};
	@Override
	public long searchCommunityCount(String query , AccessType accessType , Integer moderatorId , Integer userId ){
		boolean hasQuery = query != null && !query.isEmpty();
		boolean hasAT = accessType != null;
		boolean hasModeratorId = moderatorId != null;
		boolean hasUserId = userId != null;
		Boolean[] conditions = {hasQuery , hasAT , hasModeratorId};

		long count = Arrays.stream(conditions).filter(b->b).count();

		if(count > 1 || (hasUserId && !hasAT))
			throw new IllegalCommunitySearchArgumentsException();

		if(hasQuery || count == 0)
			return this.searchCommunityCount(query);

		if(hasAT)
			return userService.getCommunitiesByAccessTypePages(userId , accessType) * PAGE_SIZE;


		return communityService.getByModeratorCount(moderatorId);
	}


	@Override
	public List<User> searchUser(String query , int limit , int offset , String email){
		if(email != null && !email.isEmpty()){
			List<User> list = new ArrayList<>();

			try {
				User u = userService.findByEmail(email);
				list.add(u);
			}catch (Exception ignored){}

			return list;
		}
		return searchDao.searchUser(query , limit , offset);
	}
	@Override
	public long searchUserCount(String query , String email){
		if(email != null && !email.equals("")){
			try {
				User u = userService.findByEmail(email);
				return 1;
			}catch (Exception ignored){
				return 0;
			}
		}
		return searchDao.searchUserCount(query);
	}

	@Override
	public long searchCommunityCount(String query){
		return searchDao.searchCommunityCount(query);
	}

}
