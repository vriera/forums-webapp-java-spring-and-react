package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.interfaces.exceptions.AlreadyCreatedException;
import ar.edu.itba.paw.interfaces.exceptions.BadParamsException;
import ar.edu.itba.paw.interfaces.exceptions.GenericNotFoundException;
import ar.edu.itba.paw.models.AccessType;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.CommunityNotifications;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface CommunityService {
    Optional<Community> findByName(String name);
    //Lista las comunidades a las que el usuario tiene acceso
    List<Community> list(User requester);

    //Busca entre las comunidades sin importar si el usuario tiene acceso o no
    Optional<Community> findById(Long id );
    Community findByIdAndAddUserCount (Long id ) throws GenericNotFoundException;

    Optional<Community> create(String title, String description, User moderator) throws AlreadyCreatedException, BadParamsException;

    //Devuelve los usuarios miembros de la comunidad
    List<User> getMembersByAccessType(Long communityId, AccessType type, Integer page, Integer limit);
    Integer getMembersByAccessTypeCount(Long communityId, AccessType type);
    List<Community> getPublicCommunities();
    //Devuelve el tipo de acceso del usuario
    Optional<AccessType> getAccess(Long userId, Long communityId);

    //Chequea que el usuario pueda acceder a la comunidad
    boolean canAccess(User user, Community community);

    //Devuelve las páginas que se van a necesitar para plasmar los datos
    long getMemberByAccessTypePages(Long communityId, AccessType type);

    boolean setUserAccess(Long userId, Long communityId, AccessType accessType);
    boolean setAccessByModerator(Long userId, Long communityId, AccessType accessType);

    //El usuario peticiona que el moderador le permita acceso a la comunidad
/*    boolean requestAccess(Long userId, Long communityId);

    //El moderador admite al usuario en la comunidad
    boolean admitAccess(Long userId, Long communityId, Long authorizerId);

    //El moderador rechaza al usuario en la comunidad
    boolean rejectAccess(Long userId, Long communityId, Integer authorizerId);

    //Invita al usuario a la comunidad, pero la membresía está pendiente
    boolean invite(Number userId, Number communityId, Number authorizerId);

    //El usuario acepta una invitación a la comunidad
    boolean acceptInvite(Number userId, Number communityId);

    //El usuario rechaza una invitación a la comunidad
    boolean refuseInvite(Number userId, Number communityId);

    //El moderador echa al usuario de la comunidad si estaba invitado
    boolean kick(Number userId, Number communityId, Number authorizerId);

    //El moderador proscribe al usuario de la comunidad si estaba invitado
    boolean ban(Number userId, Number communityId, Number authorizerId);

    //El moderador vuelve a admitir al usuario de la comunidad si estaba invitado
    boolean liftBan(Number userId, Number communityId, Number authorizerId);

    //El usuario deja la comunidad
    boolean leaveCommunity(Number userId, Number communityId);

    //El usuario abandona la comunidad, y no se le puede volver a invitar
    boolean blockCommunity(Number userId, Number communityId);

    //El usuario, luego de abandonar la comunidad, permite que lo vuelvan a invitar
    boolean unblockCommunity(Number userId, Number communityId);*/

    List<CommunityNotifications> getCommunityNotifications(Number authorizerId);

    Optional<CommunityNotifications> getCommunityNotificationsById(Number communityId);

    Optional<Number> getUserCount(Number communityId);

    List<Community>  list(Long userId, Integer limit, Integer page);
    long listCount(Number userdId);


}
