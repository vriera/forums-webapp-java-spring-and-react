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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
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

    private final int pageSize = 10;

    private Community addUserCount(Community c) {
        Number count = this.getUserCount(c.getId());
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

        return userDao.getMembersByAccessType(communityId.longValue(), type, pageSize * page.longValue(), pageSize);
    }

    @Override
    public Optional<AccessType> getAccess(Number userId, Number communityId) {
        if (userId == null || userId.longValue() < 0 || communityId == null || communityId.longValue() < 0)
            return Optional.empty();

        return communityDao.getAccess(userId, communityId);
    }

    @Override
    public boolean canAccess(User user, Community community) {
        if (community == null)
            return false;

        boolean userIsMod = false;
        Optional<AccessType> access = Optional.empty();

        if (user != null) {
            userIsMod = user.getId() == community.getModerator().getId();
            access = this.getAccess(user.getId(), community.getId());
        }

        boolean userIsAdmitted = access.isPresent() && access.get().equals(AccessType.ADMITTED);
        boolean communityIsPublic = community.getModerator().getId() == 0;
        return communityIsPublic || userIsMod || userIsAdmitted;
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
    public long getMembersByAccessTypePages(Number communityId, AccessType type) {
        if (communityId == null || communityId.longValue() <= 0)
            throw new IllegalArgumentException("Invalid communityId: must not be null, and must be greater than 0");

        long total = userDao.getMemberByAccessTypeCount(communityId, type);
        return (total % pageSize == 0) ? total / pageSize : (total / pageSize) + 1;
    }

    private boolean invalidCredentials(Number userId, Number communityId, Number authorizerId) {
        LOGGER.debug("Credentials: userId = {}, communityId = {}, authorizedId: {}", userId, communityId, authorizerId);
        
        if (userId == null || userId.longValue() < 0 || communityId == null || communityId.longValue() < 0) {
            return true;
        }

        User user = userService.findById(userId.longValue());

        Community community = this.findById(communityId);

        // If the authorizer is not the moderator, they can't perform the action
        if (authorizerId != null && authorizerId.longValue() != community.getModerator().getId()) {
            return true;
        }
        
        // If the target user is the moderator of the community, they can't perform the action
        return isModerator(user, community);
    }

//    ---------------------------- ACCESS TYPE MODIFICATION ACTIONS --------------------------------

    @Override
    public boolean requestAccess(Number userId, Number communityId) {
        if (invalidCredentials(userId, communityId, null))
            return false;

        Optional<AccessType> maybeAccess = communityDao.getAccess(userId, communityId);

        // Si su acceso fue restringido o ya está, no quiero que vuelva a molestar
        // pidiendo que lo admitan.
        if (maybeAccess.isPresent()
                && (maybeAccess.get().equals(AccessType.BANNED) || maybeAccess.get().equals(AccessType.ADMITTED)))
            return false;

        // Permito pedidos repetidos, ahorro llamada a bd
        if (maybeAccess.isPresent() && maybeAccess.get().equals(AccessType.REQUESTED))
            return true;

        communityDao.updateAccess(userId, communityId, AccessType.REQUESTED);
        return true;
    }

    @Override
    public boolean acceptRequest(Number userId, Number communityId, Number authorizerId) {
        if (invalidCredentials(userId, communityId, authorizerId))
            return false;

        Optional<AccessType> maybeAccess = communityDao.getAccess(userId, communityId);

        // Esto solo tiene sentido cuando había pedido que lo acepten
        if (maybeAccess.isPresent() && !maybeAccess.get().equals(AccessType.REQUESTED))
            return false;
        communityDao.updateAccess(userId, communityId, AccessType.ADMITTED);
        return true;
    }

    @Override
    public boolean rejectRequest(Number userId, Number communityId, Number authorizerId) {
        if (invalidCredentials(userId, communityId, authorizerId))
            return false;

        Optional<AccessType> maybeAccess = communityDao.getAccess(userId, communityId);

        // Esto solo tiene sentido cuando había pedido entrar
        if (maybeAccess.isPresent() && !maybeAccess.get().equals(AccessType.REQUESTED))
            return false;

        communityDao.updateAccess(userId, communityId, AccessType.REQUEST_REJECTED);
        return true;
    }

    @Override
    public boolean invite(Number userId, Number communityId, Number authorizerId) {

        if (invalidCredentials(userId, communityId, authorizerId))
            return false;

        Optional<AccessType> maybeAccess = communityDao.getAccess(userId, communityId);

        // No quiero que molesten a un usuario que bloqueó la comunidad
        if (maybeAccess.isPresent() && maybeAccess.get().equals(AccessType.BLOCKED_COMMUNITY))
            return false;

        // Si ya había pedido entrar, en vez que invitarlo lo acepto. Evita un loop de
        // invito -> no la ve y pide acceso -> no lo veo y lo invito de nuevo
        if (maybeAccess.isPresent() && maybeAccess.get().equals(AccessType.REQUESTED)) {
            communityDao.updateAccess(userId, communityId, AccessType.ADMITTED);
        }

        communityDao.updateAccess(userId, communityId, AccessType.INVITED);
        return true;
    }

    @Override
    public boolean acceptInvite(Number userId, Number communityId) {
        if (invalidCredentials(userId, communityId, null))
            return false;

        Optional<AccessType> maybeAccess = communityDao.getAccess(userId, communityId);

        if (maybeAccess.isPresent() && !maybeAccess.get().equals(AccessType.INVITED))
            return false;

        communityDao.updateAccess(userId, communityId, AccessType.ADMITTED);
        return true;
    }

    @Override
    public boolean rejectInvite(Number userId, Number communityId) {
        if (invalidCredentials(userId, communityId, null))
            return false;

        Optional<AccessType> maybeAccess = communityDao.getAccess(userId, communityId);

        // Solo tiene sentido si lo invitaron
        if (maybeAccess.isPresent() && !maybeAccess.get().equals(AccessType.INVITED))
            return false;

        communityDao.updateAccess(userId, communityId, AccessType.INVITE_REJECTED);
        return true;
    }

    @Override
    public boolean kick(Number userId, Number communityId, Number authorizerId) {
        if (invalidCredentials(userId, communityId, authorizerId))
            return false;

        Optional<AccessType> maybeAccess = communityDao.getAccess(userId, communityId);

        // Esto solo tiene sentido cuando había sido admitido
        if (maybeAccess.isPresent() && !maybeAccess.get().equals(AccessType.ADMITTED))
            return false;

        communityDao.updateAccess(userId, communityId, AccessType.KICKED);
        return true;
    }

    @Override
    public boolean ban(Number userId, Number communityId, Number authorizerId) {
        if (invalidCredentials(userId, communityId, authorizerId))
            return false;

        Optional<AccessType> maybeAccess = communityDao.getAccess(userId, communityId);

        // Permito banneos repetidos, ahorro llamada a bd
        if (maybeAccess.isPresent() && maybeAccess.get().equals(AccessType.BANNED))
            return true;

        communityDao.updateAccess(userId, communityId, AccessType.BANNED);
        return true;
    }

    @Override
    public boolean liftBan(Number userId, Number communityId, Number authorizerId) {
        if (invalidCredentials(userId, communityId, authorizerId))
            return false;

        Optional<AccessType> maybeAccess = communityDao.getAccess(userId, communityId);

        // Esto solo tiene sentido cuando había sido banneado
        if (maybeAccess.isPresent() && !maybeAccess.get().equals(AccessType.BANNED))
            return false;

        communityDao.updateAccess(userId, communityId, null); // Esto restablece el acceso
        return true;
    }

    @Override
    public boolean leave(Number userId, Number communityId) {
        if (invalidCredentials(userId, communityId, null))
            return false;

        Optional<AccessType> maybeAccess = communityDao.getAccess(userId, communityId);

        // Esto solo tiene sentido cuando había sido admitido
        if (maybeAccess.isPresent() && !maybeAccess.get().equals(AccessType.ADMITTED))
            return false;

        communityDao.updateAccess(userId, communityId, AccessType.LEFT);
        return true;
    }

    @Override
    public boolean block(Number userId, Number communityId) {
        if (invalidCredentials(userId, communityId, null))
            return false;

        Optional<AccessType> maybeAccess = communityDao.getAccess(userId, communityId);

        // Esto solo tiene sentido cuando había sido admitido
        if (maybeAccess.isPresent() && !maybeAccess.get().equals(AccessType.ADMITTED))
            return false;

        communityDao.updateAccess(userId, communityId, AccessType.BLOCKED_COMMUNITY);
        return true;
    }

    @Override
    public boolean unblock(Number userId, Number communityId) {
        if (invalidCredentials(userId, communityId, null))
            return false;

        Optional<AccessType> maybeAccess = communityDao.getAccess(userId, communityId);

        // Esto solo tiene sentido cuando había sido admitido
        if (maybeAccess.isPresent() && !maybeAccess.get().equals(AccessType.BLOCKED_COMMUNITY))
            return false;

        communityDao.updateAccess(userId, communityId, null);
        return true;
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
    public Number getUserCount(Number communityId) {
        return communityDao.getUserCount(communityId).orElse(0).longValue() + 1L;
    }

    @Override
    public List<Community> list(Number userId, Number limit, Number offset) {
        return communityDao.list(userId, limit, offset).stream().map(this::addUserCount).collect(Collectors.toList());
    }

    public long listCount(Number userdId) {
        return communityDao.listCount(userdId);
    }

    @Override
    public Community findByName(String name) {
        Community c = communityDao.findByName(name).orElseThrow(NoSuchElementException::new);
        return addUserCount(c);
    }

    @Override
    public void modifyAccessType(long targetUserId, long communityId, AccessType targetAccessType){
        Community community = this.findById(communityId);
        if(targetUserId == community.getModerator().getId()){
            throw new IllegalArgumentException("The target user cannot be the community moderator");
        }

    }
}
