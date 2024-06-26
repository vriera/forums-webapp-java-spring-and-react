package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.*;

import java.util.List;

public interface SearchService {

	List<Question> searchQuestion(String query, SearchFilter filter, SearchOrder order, Long communityId, Long userId,
			Long ownerId, int page);

	long searchQuestionPagesCount(String query, SearchFilter filter, SearchOrder order, Long communityId, Long userId,
			Long ownerId);

	List<User> searchUser(String query, String email, AccessType accessType, Long communityId, int page);

	long searchUserPagesCount(String query, String email, AccessType accessType, Long communityId);

	List<Community> searchCommunity(String query, AccessType accessType, Integer moderatorId, Integer userId,
			boolean onlyAskable, int page);

	long searchCommunityPagesCount(String query, AccessType accessType, Integer moderatorId, Integer userId,
			boolean onlyAskable);

	List<Answer> searchAnswer(Long questionId, Long ownerId, int page);

	long searchAnswerPagesCount(Long questionId, Long ownerId);

}