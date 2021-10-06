package ar.edu.itba.paw.interfaces.persistance;

import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Forum;

import java.util.List;
import java.util.Optional;

public interface ForumDao {
    List<Forum> list();
    List<Forum> findByCommunity(Number communityId);
    Optional<Forum> findById(Number forumId);
    Forum create(Community community);
}
