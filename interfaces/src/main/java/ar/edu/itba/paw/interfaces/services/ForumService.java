package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Forum;

import java.util.List;
import java.util.Optional;

public interface ForumService {
    List<Forum> list();
    List<Forum> findByCommunity(Number communityId);
    Optional<Forum> findById(Number forumId);
    Optional<Forum> create(Community community);
}
