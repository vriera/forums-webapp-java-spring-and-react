package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.exceptions.AlreadyCreatedException;
import ar.edu.itba.paw.interfaces.exceptions.BadParamsException;
import ar.edu.itba.paw.interfaces.exceptions.GenericNotFoundException;
import ar.edu.itba.paw.interfaces.exceptions.GenericOperationException;
import ar.edu.itba.paw.interfaces.persistance.CommunityDao;
import ar.edu.itba.paw.interfaces.persistance.UserDao;
import ar.edu.itba.paw.interfaces.services.CommunityService;
import ar.edu.itba.paw.interfaces.services.ForumService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.AccessType;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.CommunityNotifications;
import ar.edu.itba.paw.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommunityServiceImpl implements CommunityService {

    @Autowired
    private CommunityDao communityDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private ForumService forumService;

    @Autowired
    private UserService userService;

    private static final Logger LOGGER = LoggerFactory.getLogger(CommunityServiceImpl.class);

    private final static int pageSize = 10;

    private Community addUserCount(Community c) {
        Number count = this.getUserCount(c.getId()).orElse(0);
        c.setUserCount(count.longValue());
        return c;
    }

    @Override
    public List<Community> list(User requester) {
        if (requester == null)
            return communityDao.list(-1); //Quiero las comunidades públicas

        return communityDao.list(requester.getId()).stream().map(this::addUserCount).collect(Collectors.toList());
    }

    @Override
    public Optional<Community> findById(Long communityId) {
        return communityDao.findById(communityId);
    }

    @Override
    public Community findByIdAndAddUserCount(Long id) throws GenericNotFoundException {
        Optional<Community> community = communityDao.findById(id);
        if (!community.isPresent()) throw new GenericNotFoundException("community");
        Community c = community.get();
        c.setUserCount(0L);
        Optional<Number> uc = getUserCount(id);
        uc.ifPresent(number -> c.setUserCount(number.longValue()));
        return c;
    }

    @Override
    @Transactional
    public Optional<Community> create(String name, String description, User moderator) throws AlreadyCreatedException, BadParamsException {
        if (name == null || name.isEmpty() || description == null) throw new BadParamsException("name or description");

        Optional<Community> maybeTaken = communityDao.findByName(name);
        if (maybeTaken.isPresent()) throw new AlreadyCreatedException("the name is already in use");
        Community community = communityDao.create(name, description, moderator);
        forumService.create(community); //Creo el foro default para la comunidad
        return Optional.ofNullable(community);
    }

    @Override
    public List<User> getMembersByAccessType(Long communityId, AccessType type, Integer page, Integer limit) {
        if (communityId == null || communityId <= 0 || page < 0)
            return Collections.emptyList();
        return userDao.getMembersByAccessType(communityId, type, page, limit);
    }

    @Override
    public Integer getMembersByAccessTypeCount(Long communityId, AccessType type) {
        if (communityId == null || communityId <= 0)
            return 0;
        return userDao.getMemberByAccessTypeCount(communityId, type);
    }

    @Override
    public Optional<AccessType> getAccess(Long userId, Long communityId) throws GenericNotFoundException, BadParamsException {
        if (userId == null || userId < 0 || communityId == null || communityId < 0)
            throw new BadParamsException("userId or communityId");

        Optional<Community> community = this.findById(communityId);
        if (!community.isPresent()) throw new GenericNotFoundException("community");
        if (community.get().getModerator().getId() == 0) return Optional.of(AccessType.ADMITTED);

        return communityDao.getAccess(userId, communityId);
    }


    @Override
    public boolean canAccess(User user, Community community) {
        if (community == null) return false;

        boolean userIsMod = false;
        Optional<AccessType> access = Optional.empty();

        if (user != null) {
            userIsMod = user.getId() == community.getModerator().getId();
            try {
                access = this.getAccess(user.getId(), community.getId());
            } catch (Exception e) {
                return false;
            }
        }

        boolean userIsAdmitted = access.isPresent() && access.get().equals(AccessType.ADMITTED);
        boolean communityIsPublic = community.getModerator().getId() == 0;
        return communityIsPublic || userIsMod || userIsAdmitted;
    }

    @Override
    public List<Community> getPublicCommunities() {
        return communityDao.getPublicCommunities().stream().map(this::addUserCount).collect(Collectors.toList());
    }

    @Override
    public long getMemberByAccessTypePages(Long communityId, AccessType type) {
        if (communityId == null || communityId.longValue() <= 0)
            return -1;

        long total = userDao.getMemberByAccessTypeCount(communityId, type);
        return (total % pageSize == 0) ? total / pageSize : (total / pageSize) + 1;
    }

    private static final Map<AccessType, Set<AccessType>> ACCESS_MODERATOR_TRANSITIONS = new HashMap<>();

    static {
        ACCESS_MODERATOR_TRANSITIONS.put(AccessType.BANNED, Collections.singleton(AccessType.KICKED));
        ACCESS_MODERATOR_TRANSITIONS.put(null, Collections.singleton(AccessType.INVITED));
        ACCESS_MODERATOR_TRANSITIONS.put(AccessType.INVITED, Collections.singleton(null));
        ACCESS_MODERATOR_TRANSITIONS.put(AccessType.ADMITTED, new HashSet<>(Arrays.asList(AccessType.KICKED, AccessType.BANNED)));
    }

    @Override
    public boolean setAccessByModerator(Long userId, Long communityId, AccessType accessType) throws GenericOperationException { //TODO: Spring security moderator?
        Optional<AccessType> currentAccess = communityDao.getAccess(userId, communityId);
        AccessType access = null;
        if (currentAccess.isPresent()) access = currentAccess.get();
        if (access != null && access.equals(accessType))
            throw new GenericOperationException("same access type", "SAME.ACCESS." + access.name());
        if (accessType != AccessType.BANNED &&
                ACCESS_MODERATOR_TRANSITIONS.getOrDefault(access, Collections.emptySet()).contains(accessType)) {
            communityDao.updateAccess(userId, communityId, accessType);
            return true;
        }

        return false;
    }

    private static final Map<AccessType, Set<AccessType>> ACCESS_USER_TRANSITIONS = new HashMap<>();

    static {
        ACCESS_USER_TRANSITIONS.put(AccessType.INVITED, new HashSet<>(Arrays.asList(AccessType.ADMITTED, AccessType.BLOCKED_COMMUNITY, AccessType.INVITE_REJECTED)));
        ACCESS_USER_TRANSITIONS.put(null, Collections.singleton(AccessType.REQUESTED));
        ACCESS_USER_TRANSITIONS.put(AccessType.BLOCKED_COMMUNITY, Collections.singleton(null));
        ACCESS_USER_TRANSITIONS.put(AccessType.ADMITTED, new HashSet<>(Arrays.asList(AccessType.LEFT, AccessType.BLOCKED_COMMUNITY)));
        ACCESS_USER_TRANSITIONS.put(AccessType.LEFT, new HashSet<>(Arrays.asList(AccessType.ADMITTED, AccessType.BLOCKED_COMMUNITY)));
    }

    @Override
    public boolean setUserAccess(Long userId, Long communityId, AccessType accessType) {
        Optional<AccessType> currentAccessOpt = communityDao.getAccess(userId, communityId);
        AccessType currentAccess = currentAccessOpt.orElse(null);
        if (accessType != AccessType.BANNED && ACCESS_USER_TRANSITIONS.getOrDefault(currentAccess, Collections.emptySet()).contains(accessType)) {
            communityDao.updateAccess(userId, communityId, accessType);
            return true;
        }

        return false;
    }

    @Override
    public List<CommunityNotifications> getCommunityNotifications(Number authorizerId) {
        return communityDao.getCommunityNotifications(authorizerId);
    }

    @Override
    public Optional<CommunityNotifications> getCommunityNotificationsById(Long communityId) throws GenericNotFoundException {
        Optional<Community> c = findById(communityId);
        if (!c.isPresent()) throw new GenericNotFoundException("community");
        return communityDao.getCommunityNotificationsById(communityId);
    }


    @Override
    public Optional<Number> getUserCount(Number communityId) {
        return communityDao.getUserCount(communityId);
    }


    @Override
    public List<Community> list(Long userId, Integer limit, Integer page) {
        return communityDao.list(userId, limit, page).stream().map(this::addUserCount).collect(Collectors.toList());
    }

    public Integer getCommunitiesCount(Long userdId) {
        return communityDao.listCount(userdId);
    }

    @Override
    public Optional<Community> findByName(String name) {
        return communityDao.findByName(name);
    }

}
