package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.AccessType;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.CommunityNotifications;
import ar.edu.itba.paw.models.User;

import java.util.List;

public interface CommunityService {
    //Busca entre las comunidades sin importar si el usuario tiene acceso o no
    Community findById(long id );

    Community create(String title, String description, User moderator);

    //Devuelve el tipo de acceso del usuario
    AccessType getAccess(long userId, long communityId);

    //Chequea que el usuario pueda acceder a la comunidad
    boolean canAccess(long userId, long communityId);

    // Perform the action needed to get the user to the target access type
    void modifyAccessType(long userId, long communityId, AccessType targetAccessType);

    CommunityNotifications getCommunityNotificationsById(long communityId);

    // ------------- Used by search ------------------
    //Devuelve los usuarios miembros de la comunidad
    List<User> getMembersByAccessType(long communityId, AccessType type, int page);

    // Returns the number of pages needed to display the data
    long getMembersByAccessTypePagesCount(long communityId, AccessType type);

    List<Community> getByModerator(long moderatorId, int page);

    long getByModeratorPagesCount(long moderatorId);

    //users frfr
    long getUsersCount(long communityId);

    List<Community> list(long userId , int page);
    
    long listPagesCount(long userId);
}
