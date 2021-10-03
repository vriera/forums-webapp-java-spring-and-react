package ar.edu.itba.paw.interfaces.persistance;

import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface CommunityDao {
    List<Community>  list();

    Optional<Community> findById(Number id );

    Community create(String name, String description, User moderator);

    //Devuelve las comunidades moderadas por un cierto moderador
    List<Community> getByModerator(long moderatorId, int offset, int limit);
}
