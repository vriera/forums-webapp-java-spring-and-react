package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.exceptions.BadParamsException;
import ar.edu.itba.paw.interfaces.persistance.SearchDao;
import ar.edu.itba.paw.interfaces.services.CommunityService;
import ar.edu.itba.paw.interfaces.services.QuestionService;
import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private SearchDao searchDao;
    @Autowired
    private CommunityService communityService;
    @Autowired
    private UserService userService;
    @Autowired
    private QuestionService questionService;

    @Override
    public List<Question> search(String query, SearchFilter filter, SearchOrder order, Long community, Long userId, User user, int limit, int page) throws BadParamsException {
        if (userId != null) {
            if (!queryEmpty(query) || community != null)
                throw new BadParamsException("query, filter, order or community not with userId");
            return questionService.findByUser(userId, page, limit);
        }
        if (user == null) user = new User(-1L, "", "", "");
        if (query == null || query.isEmpty()) {
            if (community == null) return searchDao.search(filter, order, -1L, user, limit, page);
            return searchDao.search(filter, order, community, user, limit, page);
        }
        return searchDao.search(query, filter, order, community, user, limit, page);
    }

    @Override
    public List<Answer> getTopAnswers(Long userId) {
        return searchDao.getTopAnswers(userId);
    }

    @Override
    public Integer countQuestionQuery(String query, SearchFilter filter, SearchOrder order, Long community, User user, Long userId) {
        if (userId != null) return questionService.findByUserCount(userId);
        if (user == null) {
            user = new User(-1L, "", "", "");
        }
        if (query == null || query.isEmpty()) {
            if (community == null) return searchDao.searchCount(filter, -1L, user).intValue();
            return searchDao.searchCount(filter, community, user).intValue();
        }
        return searchDao.searchCount(query, filter, community, user).intValue();
    }

    private boolean queryEmpty(String query) {
        return (query == null || query.equals(""));
    }

    @Override
    public List<User> searchUser(String query, AccessType accessType, Long communityId, String email, int page, int limit) throws BadParamsException {
        if (accessType != null) {
            if (communityId == null) throw new BadParamsException("accessType and communityId");
            if (email != null || !queryEmpty(query)) throw new BadParamsException("email, query and/or access type");
            return communityService.getMembersByAccessType(communityId, accessType, page, limit);
        } else if (email != null) {
            if (communityId != null || !queryEmpty(query))
                throw new BadParamsException("email, query and/or communityId");
            List<User> list = new ArrayList<>();
            Optional<User> user = userService.findByEmail(email);
            if (user.isPresent()) list.add(user.get());
            return list;
        } else return searchDao.searchUser(query, page, limit);
    }

    @Override
    public List<Community> searchCommunity(String query, Long userId, AccessType accessType, Long moderatorId, int page, int limit) throws BadParamsException {
        List<Community> communities = new ArrayList<>();
        Integer count = 0;
        if (accessType != null) {
            if (moderatorId != null || (query != null && !query.equals(""))) {
                throw new BadParamsException("The 'moderatorId', 'query' or 'accessType' must not be present at the same time");
            }
            if (userId == null) {
                throw new BadParamsException("The 'userId' is mandatory when 'accessType' is present");
            }
            communities = userService.getCommunitiesByAccessType(userId, accessType, page, limit);

            return communities;
        } else if (query != null && !query.equals("")) {
            if (userId != null || moderatorId != null) {
                throw new BadParamsException("The 'userId', 'moderatorId' or 'query' must not be present at the same time");
            }
            communities = searchDao.searchCommunity(query, page, limit);
        } else if (moderatorId != null) {
            if (userId != null)
                throw new BadParamsException("The 'userId' and 'moderatorId'  must not be present at the same time");
            communities = userService.getModeratedCommunities(moderatorId, page, limit);
        } else if (userId != null) communities = communityService.list(userId, limit, page);
        else communities = searchDao.searchCommunity("", page, limit);


        for (Community c : communities) {
            c.setUserCount(communityService.getUserCount(c.getId()).orElse(0).longValue() + 1);
        }
        return communities;
    }


    @Override
    public Integer searchCommunityCount(String query, AccessType accessType, Long userId, Long moderatorId) {
        if (accessType != null) {
            return userService.getCommunitiesByAccessTypeCount(userId, accessType).intValue();
        } else if (moderatorId != null) return userService.getModeratedCommunitiesCount(moderatorId);
        else if (query != null && !query.equals("")) return searchDao.searchCommunityCount(query);
        else if (userId != null) return communityService.getCommunitiesCount(userId);
        else return searchDao.searchCommunityCount("");
    }


    @Override
    public Integer searchUserCount(String query, AccessType accessType, Long communityId, String email) {
        if (accessType != null) {
            return communityService.getMembersByAccessTypeCount(communityId, accessType);
        } else if (email != null) {
            return 1;
        } else return searchDao.searchUserCount(query);
    }

}
