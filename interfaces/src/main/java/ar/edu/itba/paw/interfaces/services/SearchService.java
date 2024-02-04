package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.interfaces.exceptions.BadParamsException;
import ar.edu.itba.paw.models.*;

import java.util.List;

public interface SearchService {

    Integer countQuestionQuery(String query, SearchFilter filter, SearchOrder order, Long community, User user, Long userId);

    List<Question> search(String query, SearchFilter filter, SearchOrder order, Long community, Long userId, User user, int limit, int offset) throws BadParamsException;

    List<User> searchUser(String query, AccessType accessType, Long communityId, String email, int page, int limit) throws BadParamsException;

    List<Community> searchCommunity(String query, Long userId, AccessType accessType, Long moderatorId, int page, int limit) throws BadParamsException;

    Integer searchUserCount(String query, AccessType accessType, Long communityId, String email);

    Integer searchCommunityCount(String query, AccessType accessType, Long userId, Long moderatorId);

    List<Answer> getTopAnswers(Long userId);
}
