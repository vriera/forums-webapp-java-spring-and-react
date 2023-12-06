package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistance.QuestionDao;
import ar.edu.itba.paw.interfaces.persistance.SearchDao;
import ar.edu.itba.paw.interfaces.services.CommunityService;
import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.exceptions.IllegalCommunitySearchArgumentsException;
import ar.edu.itba.paw.services.utils.PaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SearchServiceImpl implements SearchService {
	private static final int PAGE_SIZE = PaginationUtils.PAGE_SIZE;
	@Autowired
	private SearchDao searchDao;

	@Autowired
	private UserService userService;

	@Autowired
	private CommunityService communityService;

	@Autowired
	private QuestionDao questionDao;
	@Override
	public List<Question> search(String query , SearchFilter filter , SearchOrder order , Number community , User user , int page) {
		if( user == null){
			user = new User(-1L , "", "" , "");
		}
		List<Question> q;
		if(query == null || query.isEmpty())
			q= searchDao.search(filter , order , community , user , PAGE_SIZE , page*PAGE_SIZE);
		q= searchDao.search(query , filter,  order, community , user , PAGE_SIZE , page*PAGE_SIZE);
		q.forEach( x -> x.setVotes((int)questionDao.getTotalVotesByQuestionId(x.getId())));
		return q;
	}

	@Override
	public List<Answer> getTopAnswers(Number userId){
		return searchDao.getTopAnswers(userId);
	}



	@Override
	public List<Community> searchCommunity(String query , int page) {
		List<Community> communities = searchDao.searchCommunity(query , PAGE_SIZE , PAGE_SIZE*page );
		for (Community c : communities) {
			//Puede ser que el numero de las comunidades haya que subirlo uno siepre
			c.setUserCount(communityService.getUsersCount(c.getId()));
		}
		return communities;
	}
	@Override
	public List<Community> searchCommunity(String query, AccessType accessType , Integer moderatorId , Integer userId , int page){
		boolean hasQuery = query != null && !query.isEmpty();
		boolean hasAT = accessType != null;
		boolean hasModeratorId = moderatorId != null;
		boolean hasUserId = userId != null;
		Boolean[] conditions = {hasQuery , hasAT , hasModeratorId};

		long count = Arrays.stream(conditions).filter(b->b).count();

		if(count > 1 || (hasUserId && !hasAT))
			throw new IllegalCommunitySearchArgumentsException();

		if(hasQuery || count == 0)
			return this.searchCommunity(query , page);
		if(hasAT)
			return userService.getCommunitiesByAccessType(userId , accessType, page);

		return communityService.getByModerator(moderatorId , page* PAGE_SIZE , page);
	};
	@Override
	public long searchCommunityPagesCount(String query , AccessType accessType , Integer moderatorId , Integer userId ){
		boolean hasQuery = query != null && !query.isEmpty();
		boolean hasAT = accessType != null;
		boolean hasModeratorId = moderatorId != null;
		boolean hasUserId = userId != null;
		Boolean[] conditions = {hasQuery , hasAT , hasModeratorId};

		long count = Arrays.stream(conditions).filter(b->b).count();

		if(count > 1 || (hasUserId && !hasAT))
			throw new IllegalCommunitySearchArgumentsException();

		if(hasQuery || count == 0)
			return this.searchCommunityPagesCount(query);

		if(hasAT)
			return userService.getCommunitiesByAccessTypePagesCount(userId , accessType) ;


		return communityService.getByModeratorPagesCount(moderatorId);
	}


	@Override
	public List<User> searchUser(String query , String email , int pages){
		if(email != null && !email.isEmpty()){
			List<User> list = new ArrayList<>();

			try {
				User u = userService.findByEmail(email);
				list.add(u);
			}catch (Exception ignored){}

			return list;
		}
		return searchDao.searchUser(query , PAGE_SIZE , PAGE_SIZE*pages);
	}
	@Override
	public long searchUserCount(String query , String email){
		if(email != null && !email.equals("")){
			try {
				userService.findByEmail(email);
				return 1;
			}catch (NoSuchElementException ignored){
				return 0;
			}
		}
		return PaginationUtils.getPagesFromTotal(searchDao.searchUserCount(query));
	}

	@Override
	public long searchCommunityPagesCount(String query){
		return PaginationUtils.getPagesFromTotal(searchDao.searchCommunityCount(query));
	}


	@Override
	public long searchQuestionPagesCount(String query , SearchFilter filter , SearchOrder order , Number community , User user ) {
		if( user == null){
			user = new User(-1L , "", "" , "");
		}
		long total;
		if(query == null || query.isEmpty()) {
			total = searchDao.searchCount(filter , community , user);
		}else {
			total = searchDao.searchCount(query, filter, community, user);
		}
		return PaginationUtils.getPagesFromTotal(total);

	}

}
