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
    List<Community> list(Long userId , Integer limit , Integer page);
    Integer listCount(Number userdId);

    List<Community> getPublicCommunities();
    Optional<Community> findById(Number id );

    Optional<Community> findByName(String name);

    Community create(String name, String description, User moderator);

    //Devuelve las comunidades moderadas por un cierto moderador
    List<Community> getByModerator(Long moderatorId, Integer page, Integer limit);

    long getByModeratorCount(Long moderatorId);

    List<Community> getCommunitiesByAccessType(Long userId, AccessType type, Integer page, Integer limit);

    long getCommunitiesByAccessTypeCount(Number userId, AccessType type);

    //Invita al usuario a la comunidad, pero la membresía está pendiente
    void updateAccess(Number userId, Number communityId, AccessType type);

    //Recupera las credenciales de acceso del usuario para una comunidad dada
    Optional<AccessType> getAccess(Long userId, Long communityId);

    List<CommunityNotifications> getCommunityNotifications(Number moderatorId);

    Optional<CommunityNotifications> getCommunityNotificationsById(Number communityId);

    Optional<Number> getUserCount(Number communityId);

}
