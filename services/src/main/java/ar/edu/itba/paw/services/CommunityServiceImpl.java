package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistance.CommunityDao;
import ar.edu.itba.paw.interfaces.persistance.UserDao;
import ar.edu.itba.paw.interfaces.services.CommunityService;
import ar.edu.itba.paw.interfaces.services.ForumService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.AccessType;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.CommunityNotifications;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.exceptions.InvalidAccessTypeChangeException;
import ar.edu.itba.paw.services.utils.PaginationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommunityServiceImpl implements CommunityService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommunityServiceImpl.class);
    private static final int PAGE_SIZE = PaginationUtils.PAGE_SIZE;
    private final Map<AccessType, AccessTypeChangeBehaviour> accessTypeChangeBehaviourMap;
    @Autowired
    private CommunityDao communityDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private ForumService forumService;
    @Autowired
    private UserService userService;

    public CommunityServiceImpl() {
        this.accessTypeChangeBehaviourMap = initializeAccessTypeChangeBehaviourMap();
    }

    private Map<AccessType, AccessTypeChangeBehaviour> initializeAccessTypeChangeBehaviourMap() {
        //TODO: Implement initialization
        Map<AccessType, AccessTypeChangeBehaviour> map = new EnumMap<>(AccessType.class);

        map.put(AccessType.REQUESTED, requestAccess);
        map.put(AccessType.REQUEST_REJECTED, rejectRequest);

        map.put(AccessType.INVITED, invite);
        map.put(AccessType.INVITE_REJECTED, rejectInvite);

        map.put(AccessType.ADMITTED, accept);

        map.put(AccessType.BANNED, ban);
        map.put(AccessType.BLOCKED, block);

        map.put(AccessType.KICKED, kick);
        map.put(AccessType.LEFT, leave);

        map.put(AccessType.NONE, reset);
        return map;
    }

    private Community addUserCount(Community c) {
        Number count = this.getUsersCount(c.getId());
        c.setUserCount(count.longValue());
        return c;
    }

    @Override
    public List<Community> list(User requester) {
        if (requester == null)
            return communityDao.list(-1); // Quiero las comunidades públicas

        return communityDao.list(requester.getId()).stream().map(this::addUserCount).collect(Collectors.toList());
    }

    @Override
    public Community findById(Number communityId) {
        return addUserCount(communityDao.findById(communityId).orElseThrow(NoSuchElementException::new));
    }

    @Override
    @Transactional
    public Community create(String name, String description, User moderator) {
        if (name == null || name.isEmpty() || description == null) {
            throw new IllegalArgumentException();
        }

        Optional<Community> maybeTaken = communityDao.findByName(name);
        if (maybeTaken.isPresent())
            throw new IllegalArgumentException();

        Community community = communityDao.create(name, description, moderator);
        forumService.create(community); // Create the default forum for the community

        return community;
    }

    @Override
    public List<User> getMembersByAccessType(Number communityId, AccessType type, Number page) {
        if (communityId == null || communityId.longValue() <= 0 || page.intValue() < 0)
            throw new IllegalArgumentException("Invalid communityId or page");

        Community community = this.findById(communityId);

        if (community.getModerator().getId() == 0)
            throw new IllegalArgumentException("The community is public");

        return userDao.getMembersByAccessType(communityId.longValue(), type, PAGE_SIZE * page.longValue(), PAGE_SIZE);
    }

    @Override
    public boolean isModerator(User u, Community c) {
        if (c == null || u == null)
            return false;
        return c.getModerator().getId() == u.getId();
    }

    @Override
    public List<Community> getPublicCommunities() {
        return communityDao.getPublicCommunities().stream().map(this::addUserCount).collect(Collectors.toList());
    }

    @Override
    public long getMembersByAccessTypePagesCount(Number communityId, AccessType type) {
        if (communityId == null || communityId.longValue() <= 0)
            throw new IllegalArgumentException("Invalid communityId: must not be null, and must be greater than 0");

        long total = userDao.getMemberByAccessTypeCount(communityId, type);
        return PaginationUtils.getPagesFromTotal(total);
    }

    private boolean invalidCredentials(Number userId, Number communityId, Number authorizerId) {
        LOGGER.debug("Credentials: userId = {}, communityId = {}, authorizedId: {}", userId, communityId, authorizerId);

        if (userId == null || userId.longValue() < 0 || communityId == null || communityId.longValue() < 0) {
            return true;
        }

        User user = userService.findById(userId.longValue());

        Community community = this.findById(communityId);

        // If the target user is the moderator of the community, they can't perform the action
        return isModerator(user, community);
    }

    @Override
    public List<CommunityNotifications> getCommunityNotifications(Number authorizerId) {
        return communityDao.getCommunityNotifications(authorizerId);
    }

    @Override
    public CommunityNotifications getCommunityNotificationsById(Number communityId) {
        Community c = findById(communityId);

        Optional<CommunityNotifications> cn = communityDao.getCommunityNotificationsById(communityId);
        if (cn.isPresent())
            return cn.get();
        User moderator = c.getModerator();
        CommunityNotifications emptyNotifications = new CommunityNotifications();
        emptyNotifications.setNotifications(0L);
        emptyNotifications.setCommunity(c);
        emptyNotifications.setModerator(moderator);
        return emptyNotifications;
    }

    @Override
    public long getUsersCount(Number communityId) {
        //we add one accounting for the moderator
        return communityDao.getUserCount(communityId).orElse(0L) + 1L;
    }

    @Override
    public List<Community> list(Number userId, Number limit, Number offset) {
        return communityDao.list(userId, limit, offset).stream().map(this::addUserCount).collect(Collectors.toList());
    }

    public long listPagesCount(Number userdId) {
        return PaginationUtils.getPagesFromTotal(communityDao.listCount(userdId));
    }

    @Override
    public Community findByName(String name) {
        Community c = communityDao.findByName(name).orElseThrow(NoSuchElementException::new);
        return addUserCount(c);
    }

    @Override
    public AccessType getAccess(Number userId, Number communityId) {
        if (userId == null || userId.longValue() < 0 || communityId == null || communityId.longValue() < 0)
            throw new IllegalArgumentException("Invalid user or community");

        return communityDao.getAccess(userId, communityId).orElse(AccessType.NONE);
    }

    @Override
    public boolean canAccess(User user, Community community) {
        if (community == null)
            return false;

        boolean userIsTheModerator = false;
        AccessType access = AccessType.NONE;

        if (user != null) { // The user is logged in
            userIsTheModerator = user.equals(community.getModerator());
            access = this.getAccess(user.getId(), community.getId());
        }

        boolean userIsAdmitted = access.equals(AccessType.ADMITTED);
        boolean communityIsPublic = community.getModerator().getId() == 0;

        return communityIsPublic || userIsTheModerator || userIsAdmitted;
    }

    @Override
    public void modifyAccessType(long targetUserId, long communityId, AccessType targetAccessType) {
        if (targetAccessType == null)
            throw new IllegalArgumentException("The target access type cannot be null");

        Community community = this.findById(communityId);
        if (targetUserId == community.getModerator().getId()) {
            throw new IllegalArgumentException("The target user cannot be the community moderator");
        }

        AccessType currentAccessType = communityDao.getAccess(targetUserId, communityId).orElse(AccessType.NONE);

        LOGGER.info("Changing access type of user {} to community {} from {} to {}", targetUserId, communityId, currentAccessType, targetAccessType);

        accessTypeChangeBehaviourMap.get(targetAccessType).changeAccessType(targetUserId, communityId, currentAccessType);
    }

    //    ---------------------------- ACCESS TYPE MODIFICATION ACTIONS --------------------------------
    private interface AccessTypeChangeBehaviour {
        void changeAccessType(long userId, long communityId, AccessType originAccessType) throws InvalidAccessTypeChangeException;
    }

    // The user requests the moderator to allow him to access the community
    private final AccessTypeChangeBehaviour requestAccess = (long userId, long communityId, AccessType originAccessType) -> {

        if (!(originAccessType.equals(AccessType.NONE) || originAccessType.equals(AccessType.BLOCKED)
                || originAccessType.equals(AccessType.KICKED) || originAccessType.equals(AccessType.LEFT)
                || originAccessType.equals(AccessType.REQUEST_REJECTED) || originAccessType.equals(AccessType.INVITE_REJECTED)))
            throw new InvalidAccessTypeChangeException(originAccessType, AccessType.REQUESTED);

        communityDao.updateAccess(userId, communityId, AccessType.REQUESTED);
    };
    // The moderator rejects the user's request to access the community
    private final AccessTypeChangeBehaviour rejectRequest = (long userId, long communityId, AccessType originAccessType) -> {
        if (!originAccessType.equals(AccessType.REQUESTED))
            throw new InvalidAccessTypeChangeException(originAccessType, AccessType.REQUEST_REJECTED);

        communityDao.updateAccess(userId, communityId, AccessType.REQUEST_REJECTED);
    };
    // The moderator invites the user to the community, but the admission is pending
    private final AccessTypeChangeBehaviour invite = (long userId, long communityId, AccessType originAccessType) -> {
        if (!(originAccessType.equals(AccessType.NONE) || originAccessType.equals(AccessType.BANNED)
                || originAccessType.equals(AccessType.INVITE_REJECTED) || originAccessType.equals(AccessType.REQUEST_REJECTED)
                || originAccessType.equals(AccessType.LEFT) || originAccessType.equals(AccessType.KICKED)))
            throw new InvalidAccessTypeChangeException(originAccessType, AccessType.INVITED);

        communityDao.updateAccess(userId, communityId, AccessType.INVITED);
    };
    // The user rejects an invitation to the community
    private final AccessTypeChangeBehaviour rejectInvite = (long userId, long communityId, AccessType originAccessType) -> {
        if (!originAccessType.equals(AccessType.INVITED))
            throw new InvalidAccessTypeChangeException(originAccessType, AccessType.INVITE_REJECTED);

        communityDao.updateAccess(userId, communityId, AccessType.INVITE_REJECTED);
    };
    // The user accepts an invitation to the community or the moderator accepts the user's request
    private final AccessTypeChangeBehaviour accept = (long userId, long communityId, AccessType originAccessType) -> {
        if (!originAccessType.equals(AccessType.INVITED) && !originAccessType.equals(AccessType.REQUESTED))
            throw new InvalidAccessTypeChangeException(originAccessType, AccessType.ADMITTED);

        communityDao.updateAccess(userId, communityId, AccessType.ADMITTED);
    };
    // The moderator kicks the user out of the community
    private final AccessTypeChangeBehaviour kick = (long userId, long communityId, AccessType originAccessType) -> {
        if (!originAccessType.equals(AccessType.ADMITTED))
            throw new InvalidAccessTypeChangeException(originAccessType, AccessType.KICKED);

        communityDao.updateAccess(userId, communityId, AccessType.KICKED);
    };
    // The moderator bans the user from the community
    private final AccessTypeChangeBehaviour ban = (long userId, long communityId, AccessType originAccessType) -> {
        if (!originAccessType.equals(AccessType.ADMITTED))
            throw new InvalidAccessTypeChangeException(originAccessType, AccessType.BANNED);

        communityDao.updateAccess(userId, communityId, AccessType.BANNED);
    };
    // The moderator lifts the ban placed on the user or the user unblocks the community
    // or the user, after blocking the community, allows themselves to be invited again
    private final AccessTypeChangeBehaviour reset = (long userId, long communityId, AccessType originAccessType) -> {
        if (!(originAccessType.equals(AccessType.BANNED) || originAccessType.equals(AccessType.BLOCKED)))
            throw new InvalidAccessTypeChangeException(originAccessType, AccessType.NONE);

        communityDao.updateAccess(userId, communityId, AccessType.NONE);
    };
    // The user leaves the community, though they can request access or be invited into it again
    private final AccessTypeChangeBehaviour leave = (long userId, long communityId, AccessType originAccessType) -> {
        // Esto solo tiene sentido cuando había sido admitido
        if (!originAccessType.equals(AccessType.ADMITTED))
            throw new InvalidAccessTypeChangeException(originAccessType, AccessType.LEFT);

        communityDao.updateAccess(userId, communityId, AccessType.LEFT);
    };
    // The user leaves the community, and cannot be invited again
    private final AccessTypeChangeBehaviour block = (long userId, long communityId, AccessType originAccessType) -> {
        // Esto solo tiene sentido cuando había sido admitido
        if (!originAccessType.equals(AccessType.ADMITTED))
            throw new InvalidAccessTypeChangeException(originAccessType, AccessType.BLOCKED);

        communityDao.updateAccess(userId, communityId, AccessType.BLOCKED);
    };


    @Override
    public List<Community> getByModerator(Number moderatorId, Number offset, Number limit){
        return communityDao.getByModerator(moderatorId , offset ,limit);
    };
    @Override
    public long getByModeratorPagesCount(Number moderatorId){
        return PaginationUtils.getPagesFromTotal(communityDao.getByModeratorCount(moderatorId));
    };
}
