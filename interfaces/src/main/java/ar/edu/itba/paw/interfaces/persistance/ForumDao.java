package ar.edu.itba.paw.interfaces.persistance;

import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Forum;

import java.util.List;

public interface ForumDao {
    List<Forum> list();
    List<Forum> findByCommunity(Community community);

}
