package ar.edu.itba.paw.interfaces.persistance;

import ar.edu.itba.paw.models.Community;
import java.util.List;
import java.util.Optional;

public interface CommunityDao {
    List<Community>  list();
    Optional<Community> findById(Number id );
}
