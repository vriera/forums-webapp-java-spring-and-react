package ar.edu.itba.paw.interfaces.persistance;

import ar.edu.itba.paw.models.AccessType;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.CommunityNotifications;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface CommunityDao {
    List<Community>  list(long userId , int limit  , int offset);

    long listCount(long userId);

    Optional<Community> findById(long communityId);

    Optional<Community> findByName(String name);

    Community create(String name, String description, User moderator);

    //Devuelve las comunidades moderadas por un cierto moderador
    List<Community> getByModerator(long moderatorId, int offset, int limit);

    long getByModeratorCount(long moderatorId);

    List<Community> getCommunitiesByAccessType(long userId, AccessType type, int offset, int limit);

    long getCommunitiesByAccessTypeCount(long userId, AccessType type);

    //Invita al usuario a la comunidad, pero la membresía está pendiente
    void updateAccess(long userId, long communityId, AccessType type);

    //Recupera las credenciales de acceso del usuario para una comunidad dada
    Optional<AccessType> getAccess(long userId, long communityId);

    Optional<CommunityNotifications> getCommunityNotificationsById(long communityId);

    Optional<Long> getUserCount(long communityId);

}
