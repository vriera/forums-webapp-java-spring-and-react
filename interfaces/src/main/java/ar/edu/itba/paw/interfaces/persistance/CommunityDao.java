package ar.edu.itba.paw.interfaces.persistance;

import ar.edu.itba.paw.models.AccessType;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface CommunityDao {
    List<Community>  list();

    Optional<Community> findById(Number id );

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
}
