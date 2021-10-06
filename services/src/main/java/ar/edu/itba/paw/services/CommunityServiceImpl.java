package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistance.CommunityDao;
import ar.edu.itba.paw.interfaces.persistance.UserDao;
import ar.edu.itba.paw.interfaces.services.CommunityService;
import ar.edu.itba.paw.interfaces.services.ForumService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.AccessType;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

    private final int pageSize = 10;

    @Override
    public List<Community> list(){ return communityDao.list();}

    @Override
    public Optional<Community> findById(Number id ){
        return communityDao.findById(id);
    };

    @Override
    @Transactional
    public Optional<Community> create(String name, String description, User moderator){
        if(name == null || name.isEmpty() || description == null || description.isEmpty()){
            return Optional.empty();
        }
        Community community = communityDao.create(name, description, moderator);
        forumService.create(community);
        return Optional.ofNullable(community);
    }

    @Override
    public List<User> getMembersByAccessType(Number communityId, AccessType type, Number page) {
        if(communityId == null || communityId.longValue() <= 0 || page.intValue() < 0)
            return Collections.emptyList();
        return userDao.getMembersByAccessType(communityId.longValue(), type, pageSize*page.longValue(), pageSize);
    }

    @Override
    public Optional<AccessType> getAccess(Number userId, Number communityId) {
        if(userId == null || communityId == null || userId.longValue() < 0 || communityId.longValue() < 0)
            return Optional.empty();

        return communityDao.getAccess(userId, communityId);
    }

    @Override
    public boolean canAccess(Optional<User> user, Community community) {
        if(community == null)
            return false;

        boolean communityIsPublic = community.getModerator().getId() == 0; //Las comunidades públicas son creadas por el usuario inyectado con id 0
        boolean userIsMod = user.isPresent() && user.get().getId() == community.getModerator().getId();
        Optional<AccessType> access;
        if(user.isPresent())
            access = this.getAccess(user.get().getId(), community.getId());
        else
            access = this.getAccess(null, community.getId());

        boolean userIsAdmitted = access.isPresent() && access.get().equals(AccessType.ADMITTED);

        return communityIsPublic || userIsMod || userIsAdmitted;
    }

    @Override
    public long getMemberByAccessTypePages(Number communityId, AccessType type) {
        if(communityId == null || communityId.longValue() <= 0)
            return -1;

        long total = userDao.getMemberByAccessTypeCount(communityId, type);
        return (total%pageSize == 0)? total/pageSize : (total/pageSize)+1;
    }

    private boolean invalidCredentials(Number userId, Number communityId){
        if(userId == null || userId.longValue() < 0 || communityId == null || communityId.longValue() < 0)
            return true;

        Optional<User> maybeUser = userService.findById(userId.longValue());
        Optional<Community> maybeCommunity = this.findById(communityId);


        return !maybeUser.isPresent() || !maybeCommunity.isPresent() ||  maybeUser.get().getId() == maybeCommunity.get().getModerator().getId();
    }
    @Override
    public boolean requestAccess(Number userId, Number communityId) {
        if(invalidCredentials(userId, communityId))
            return false;

        Optional<AccessType> maybeAccess = communityDao.getAccess(userId, communityId);

        //Si su acceso fue restringido o ya está, no quiero que vuelva a molestar pidiendo que lo admitan.
        if(maybeAccess.isPresent() && (maybeAccess.get().equals(AccessType.BANNED) || maybeAccess.get().equals(AccessType.ADMITTED)))
            return false;

        //Permito pedidos repetidos, ahorro llamada a bd
        if(maybeAccess.isPresent() && maybeAccess.get().equals(AccessType.REQUESTED))
            return true;

        communityDao.updateAccess(userId, communityId, AccessType.REQUESTED);
        return true;
    }

    @Override
    public boolean admitAccess(Number userId, Number communityId, User authorizer) {
        if(invalidCredentials(userId, communityId))
            return false;

        Optional<AccessType> maybeAccess = communityDao.getAccess(userId, communityId);

        //Esto solo tiene sentido cuando había pedido que lo acepten
        if(maybeAccess.isPresent() && !maybeAccess.get().equals(AccessType.REQUESTED))
            return false;

        communityDao.updateAccess(userId, communityId, AccessType.ADMITTED);
        return true;
    }

    @Override
    public boolean rejectAccess(Number userId, Number communityId, User authorizer) {
        if(invalidCredentials(userId, communityId))
            return false;

        Optional<AccessType> maybeAccess = communityDao.getAccess(userId, communityId);

        //Esto solo tiene sentido cuando había pedido entrar
        if(maybeAccess.isPresent() && !maybeAccess.get().equals(AccessType.REQUESTED))
            return false;

        communityDao.updateAccess(userId, communityId, AccessType.REQUEST_REJECTED);
        return true;
    }

    @Override
    public boolean invite(Number userId, Number communityId) {
        if(invalidCredentials(userId, communityId))
            return false;

        Optional<AccessType> maybeAccess = communityDao.getAccess(userId, communityId);

        //No quiero que molesten a un usuario que bloqueó la comunidad
        if(maybeAccess.isPresent() && maybeAccess.get().equals(AccessType.BLOCKED_COMMUNITY))
            return false;

        //Si ya había pedido entrar, en vez que invitarlo lo acepto. Evita un loop de invito -> no la ve y pide acceso -> no lo veo y lo invito de nuevo
        if(maybeAccess.isPresent() && maybeAccess.get().equals(AccessType.REQUESTED)){
            communityDao.updateAccess(userId, communityId, AccessType.ADMITTED);
        }

        communityDao.updateAccess(userId, communityId, AccessType.INVITED);
        return true;
    }

    @Override
    public boolean acceptInvite(Number userId, Number communityId) {
        if(invalidCredentials(userId, communityId))
            return false;

        Optional<AccessType> maybeAccess = communityDao.getAccess(userId, communityId);

        //Solo tiene sentido si lo invitaron
        if(maybeAccess.isPresent() && !maybeAccess.get().equals(AccessType.INVITED))
            return false;

        communityDao.updateAccess(userId, communityId, AccessType.ADMITTED);
        return true;
    }

    @Override
    public boolean refuseInvite(Number userId, Number communityId) {
        if(invalidCredentials(userId, communityId))
            return false;

        Optional<AccessType> maybeAccess = communityDao.getAccess(userId, communityId);

        //Solo tiene sentido si lo invitaron
        if(maybeAccess.isPresent() && !maybeAccess.get().equals(AccessType.INVITED))
            return false;

        communityDao.updateAccess(userId, communityId, AccessType.INVITE_REJECTED);
        return true;
    }

    @Override
    public boolean kick(Number userId, Number communityId, User authorizer) {
        if(invalidCredentials(userId, communityId))
            return false;

        Optional<AccessType> maybeAccess = communityDao.getAccess(userId, communityId);

        //Esto solo tiene sentido cuando había sido admitido
        if(maybeAccess.isPresent() && !maybeAccess.get().equals(AccessType.ADMITTED))
            return false;

        communityDao.updateAccess(userId, communityId, AccessType.KICKED);
        return true;
    }

    @Override
    public boolean ban(Number userId, Number communityId, User authorizer) {
        if(invalidCredentials(userId, communityId))
            return false;

        Optional<AccessType> maybeAccess = communityDao.getAccess(userId, communityId);

        //Permito banneos repetidos, ahorro llamada a bd
        if(maybeAccess.isPresent() && maybeAccess.get().equals(AccessType.BANNED))
            return true;

        communityDao.updateAccess(userId, communityId, AccessType.BANNED);
        return true;
    }

    @Override
    public boolean liftBan(Number userId, Number communityId, User authorizer) {
        if(invalidCredentials(userId, communityId))
            return false;

        Optional<AccessType> maybeAccess = communityDao.getAccess(userId, communityId);

        //Esto solo tiene sentido cuando había sido banneado
        if(maybeAccess.isPresent() && !maybeAccess.get().equals(AccessType.BANNED))
            return false;

        communityDao.updateAccess(userId, communityId, null); //Esto restablece el acceso
        return true;
    }

    @Override
    public boolean leaveCommunity(Number userId, Number communityId) {
        if(invalidCredentials(userId, communityId))
            return false;

        Optional<AccessType> maybeAccess = communityDao.getAccess(userId, communityId);

        //Esto solo tiene sentido cuando había sido admitido
        if(maybeAccess.isPresent() && !maybeAccess.get().equals(AccessType.ADMITTED))
            return false;

        communityDao.updateAccess(userId, communityId, AccessType.LEFT);
        return true;
    }

    @Override
    public boolean blockCommunity(Number userId, Number communityId) {
        if(invalidCredentials(userId, communityId))
            return false;

        Optional<AccessType> maybeAccess = communityDao.getAccess(userId, communityId);

        //Esto solo tiene sentido cuando había sido admitido
        if(maybeAccess.isPresent() && !maybeAccess.get().equals(AccessType.ADMITTED))
            return false;

        communityDao.updateAccess(userId, communityId, AccessType.BLOCKED_COMMUNITY);
        return true;
    }

    @Override
    public boolean unblockCommunity(Number userId, Number communityId) {
        if(invalidCredentials(userId, communityId))
            return false;

        Optional<AccessType> maybeAccess = communityDao.getAccess(userId, communityId);

        //Esto solo tiene sentido cuando había sido admitido
        if(maybeAccess.isPresent() && !maybeAccess.get().equals(AccessType.BLOCKED_COMMUNITY))
            return false;

        communityDao.updateAccess(userId, communityId, null);
        return true;
    }
}
