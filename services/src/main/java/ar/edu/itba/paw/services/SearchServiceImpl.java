package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistance.QuestionDao;
import ar.edu.itba.paw.interfaces.persistance.SearchDao;
import ar.edu.itba.paw.interfaces.services.AnswersService;
import ar.edu.itba.paw.interfaces.services.CommunityService;
import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.exceptions.IllegalAnswersSearchArgumentException;
import ar.edu.itba.paw.models.exceptions.IllegalCommunitySearchArgumentsException;
import ar.edu.itba.paw.models.exceptions.IllegalQuestionSearchArgumentsException;
import ar.edu.itba.paw.models.exceptions.IllegalUsersSearchArgumentsException;
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

	@Autowired
	private AnswersService answersService;

	/*
	 * Search Questions
	 */
	private List<Question> searchQuestionNoVotes(String query, SearchFilter filter, SearchOrder order, Long communityId,
			Long userId, Long ownerId, int page) {

		boolean hasOwnerId = ownerId != null;
		boolean hasAnyQueryParam = filter != null || order != null || communityId != null || userId != null;

		if (hasOwnerId && hasAnyQueryParam)
			throw new IllegalQuestionSearchArgumentsException();

		if (hasOwnerId)
			return userService.getQuestions(ownerId, page);

		long userIdQuery = userId == null ? -1 : userId;

		// default order and filters
		filter = filter == null ? SearchFilter.NONE : filter;
		order = order == null ? SearchOrder.MOST_RECENT : order;
		if (query == null || query.isEmpty())
			return searchDao.search(filter, order, communityId, userIdQuery, PAGE_SIZE, page * PAGE_SIZE);

		return searchDao.search(query, filter, order, communityId, userIdQuery, PAGE_SIZE, page * PAGE_SIZE);
	}

	@Override
	public List<Question> searchQuestion(String query, SearchFilter filter, SearchOrder order, Long communityId,
			Long userId, Long ownerId, int page) {
		List<Question> q = searchQuestionNoVotes(query, filter, order, communityId, userId, ownerId, page);
		q.forEach(x -> x.setVotes((int) questionDao.getTotalVotesByQuestionId(x.getId())));
		return q;
	}

	@Override
	public long searchQuestionPagesCount(String query, SearchFilter filter, SearchOrder order, Long communityId,
			Long userId, Long ownerId) {
		boolean hasOwnerId = ownerId != null;
		boolean hasAnyQueryParam = filter != null || order != null || communityId != null || userId != null;

		if (hasOwnerId && hasAnyQueryParam)
			throw new IllegalQuestionSearchArgumentsException();

		if (hasOwnerId)
			return userService.getQuestionsPagesCount(ownerId);

		// que siga como antes
		long userIdQuery = userId == null ? -1 : userId;
		long total;
		// default order and filters
		filter = filter == null ? SearchFilter.NONE : filter;
		if (query == null || query.isEmpty())
			return PaginationUtils.getPagesFromTotal(searchDao.searchCount(filter, communityId, userIdQuery));

		return PaginationUtils.getPagesFromTotal(searchDao.searchCount(query, filter, communityId, userIdQuery));

	}

	/*
	 * Search Communities
	 */

	@Override
	public List<Community> searchCommunity(String query, AccessType accessType, Integer moderatorId, Integer userId,
			boolean onlyAskable, int page) {
		List<Community> communities = searchCommunityNoUsersCount(query, accessType, moderatorId, userId, onlyAskable,
				page);
		for (Community c : communities) {
			// Puede ser que el numero de las comunidades haya que subirlo uno siempre
			if (c.getUserCount() == null)
				c.setUserCount(communityService.getUsersCount(c.getId()));
		}
		return communities;
	}

	private List<Community> searchCommunityNoUsersCount(String query, AccessType accessType, Integer moderatorId,
			Integer userId, boolean onlyAskable, int page) {
		boolean hasQuery = query != null && !query.isEmpty();
		boolean hasAT = accessType != null;
		boolean hasModeratorId = moderatorId != null;
		boolean hasUserId = userId != null;
		Boolean[] conditions = { hasQuery, hasAT, hasModeratorId, onlyAskable };

		long count = Arrays.stream(conditions).filter(b -> b).count();

		if (count > 1)
			throw new IllegalCommunitySearchArgumentsException();

		if ((hasAT || onlyAskable) && !hasUserId)
			throw new IllegalCommunitySearchArgumentsException();

		if (!(hasAT || onlyAskable) && hasUserId)
			throw new IllegalCommunitySearchArgumentsException();

		if (onlyAskable)
			return communityService.list(userId, page);
		if (hasModeratorId)
			return communityService.getByModerator(moderatorId, page);

		if (hasAT)
			return userService.getCommunitiesByAccessType(userId, accessType, page);

		return searchDao.searchCommunity(query, PAGE_SIZE, PAGE_SIZE * page);

	}

	@Override
	public long searchCommunityPagesCount(String query, AccessType accessType, Integer moderatorId, Integer userId,
			boolean onlyAskable) {
		boolean hasQuery = query != null && !query.isEmpty();
		boolean hasAT = accessType != null;
		boolean hasModeratorId = moderatorId != null;
		boolean hasUserId = userId != null;
		Boolean[] conditions = { hasQuery, hasAT, hasModeratorId, onlyAskable };

		long count = Arrays.stream(conditions).filter(b -> b).count();

		if (count > 1)
			throw new IllegalCommunitySearchArgumentsException();

		if ((hasAT || onlyAskable) && !hasUserId)
			throw new IllegalCommunitySearchArgumentsException();

		if (!(hasAT || onlyAskable) && hasUserId)
			throw new IllegalCommunitySearchArgumentsException();

		if (hasModeratorId)
			return communityService.getByModeratorPagesCount(moderatorId);

		if (hasAT)
			return userService.getCommunitiesByAccessTypePagesCount(userId, accessType);

		if (onlyAskable)
			return communityService.listPagesCount(userId);

		return PaginationUtils.getPagesFromTotal(searchDao.searchCommunityCount(query == null ? "" : query));
	}

	/*
	 * Search users
	 */

	@Override
	public List<User> searchUser(String query, String email, AccessType accessType, Long communityId, int page) {
		boolean hasQuery = query != null && !query.isEmpty();
		boolean hasEmail = email != null && !email.isEmpty();
		boolean hasAT = accessType != null;
		boolean hasCommunityId = communityId != null;

		// Check if there are more than one of the exclusive search
		Boolean[] conditions = { hasQuery, hasEmail, hasAT };
		long count = Arrays.stream(conditions).filter(x -> x).count();

		if (count > 1 || (hasCommunityId && !hasAT) || (hasAT && !hasCommunityId))
			throw new IllegalUsersSearchArgumentsException();

		if (hasEmail)
			try {
				return Collections.singletonList(userService.findByEmail(email));
			} catch (NoSuchElementException ignored) {
				return Collections.emptyList();
			}

		if (hasAT)
			return communityService.getMembersByAccessType(communityId, accessType, page);

		return searchDao.searchUser(query == null ? "" : query, PAGE_SIZE, PAGE_SIZE * page);

	}

	@Override
	public long searchUserPagesCount(String query, String email, AccessType accessType, Long communityId) {
		boolean hasQuery = query != null && !query.isEmpty();
		boolean hasEmail = email != null && !email.isEmpty();
		boolean hasAT = accessType != null;
		boolean hasCommunityId = communityId != null;

		// Check if there are more than one of the exclusive search
		Boolean[] conditions = { hasQuery, hasEmail, hasAT };
		long count = Arrays.stream(conditions).filter(x -> x).count();

		if (count > 1 || (hasCommunityId && !hasAT) || (hasAT && !hasCommunityId))
			throw new IllegalUsersSearchArgumentsException();

		if (hasEmail) {
			try {
				userService.findByEmail(email);
				return 1;
			} catch (NoSuchElementException ignored) {
				return 0;
			}
		}

		if (hasAT)
			return communityService.getMembersByAccessTypePagesCount(communityId, accessType);

		return PaginationUtils.getPagesFromTotal(searchDao.searchUserCount(query == null ? "" : query));
	}

	@Override
	public List<Answer> searchAnswer(Long questionId, Long ownerId, int page) {
		boolean hasQuestionId = questionId != null;
		boolean hasOwnerId = ownerId != null;
		if (hasOwnerId && hasQuestionId)
			throw new IllegalAnswersSearchArgumentException();
		if (hasOwnerId)
			return userService.getAnswers(ownerId, page);
		if (hasQuestionId)
			return answersService.findByQuestion(questionId, page);

		throw new IllegalAnswersSearchArgumentException();

	}

	@Override
	public long searchAnswerPagesCount(Long questionId, Long ownerId) {
		boolean hasQuestionId = questionId != null;
		boolean hasOwnerId = ownerId != null;
		if (hasOwnerId && hasQuestionId)
			throw new IllegalAnswersSearchArgumentException();
		if (hasOwnerId)
			return userService.getAnswersPagesCount(ownerId);
		if (hasQuestionId)
			return answersService.findByQuestionPagesCount(questionId);

		throw new IllegalAnswersSearchArgumentException();
	}

}
