package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.AccessType;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.CommunityNotifications;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.exceptions.InvalidAccessTypeChangeException;

import java.util.List;

public interface CommunityService {
    Community findByName(String name);
    //Lista las comunidades a las que el usuario tiene acceso
    List<Community> list(User requester);

    //Busca entre las comunidades sin importar si el usuario tiene acceso o no
    Community findById(Number id );

    Community create(String title, String description, User moderator) throws IllegalArgumentException;

    //Devuelve los usuarios miembros de la comunidad
    List<User> getMembersByAccessType(Number communityId, AccessType type, Number page);

    List<Community> getPublicCommunities();

    //Devuelve el tipo de acceso del usuario
    AccessType getAccess(Number userId, Number communityId);

    //Chequea que el usuario pueda acceder a la comunidad
    boolean canAccess(User user, Community community);

    boolean isModerator(User user, Community community);

    // Returns the number of pages needed to display the data
    long getMembersByAccessTypePagesCount(Number communityId, AccessType type);

    // Perform the action needed to get the user to the target access type
    void modifyAccessType(long userId, long communityId, AccessType targetAccessType) throws InvalidAccessTypeChangeException;

    List<Community> getByModerator(Number moderatorId, int page);

    long getByModeratorPagesCount(Number moderatorId);

    //FIXME: This method should be called or deleted
    List<CommunityNotifications> getCommunityNotifications(Number authorizerId);

    CommunityNotifications getCommunityNotificationsById(Number communityId);

    //users frfr
    long getUsersCount(Number communityId);

    List<Community>  list(Number userId , int page);
    
    long listPagesCount(Number userId);
}
