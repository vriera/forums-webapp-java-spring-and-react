package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Forum;

import java.util.List;

public interface ForumService {
    List<Forum> list();
    List<Forum> findByCommunity(Number communityId);
}
