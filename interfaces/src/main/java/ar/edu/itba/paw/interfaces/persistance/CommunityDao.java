package ar.edu.itba.paw.interfaces.persistance;

import ar.edu.itba.paw.models.Community;
import java.util.List;

public interface CommunityDao {
    List<Community>  list();
}
