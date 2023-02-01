package ar.edu.itba.paw.interfaces.persistance;

import ar.edu.itba.paw.models.AccessType;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.CommunityNotifications;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface CommunityDao {
    //Devuelve las comunidades a las que el usuario tiene acceso, si le paso -1 levanta solo las públicas
    List<Community>  list(Number userId);
    List<Community>  list(Number userId , Number limit  , Number offset);
    long listCount(Number userdId);

    List<Community> getPublicCommunities();
    Optional<Community> findById(Number id );

    Optional<Community> findByName(String name);

    Community create(String name, String description, User moderator);

    //Devuelve las comunidades moderadas por un cierto moderador
    List<Community> getByModerator(Number moderatorId, Number offset, Number limit);

    long getByModeratorCount(Number moderatorId);

    List<Community> getCommunitiesByAccessType(Number userId, AccessType type, Number offset, Number limit);

    long getCommunitiesByAccessTypeCount(Number userId, AccessType type);

    //Invita al usuario a la comunidad, pero la membresía está pendiente
    void updateAccess(Number userId, Number communityId, AccessType type);

    //Recupera las credenciales de acceso del usuario para una comunidad dada
    Optional<AccessType> getAccess(Number userId, Number communityId);

    List<CommunityNotifications> getCommunityNotifications(Number moderatorId);

    Optional<CommunityNotifications> getCommunityNotificationsById(Number communityId);

    Optional<Number> getUserCount(Number communityId);

}
