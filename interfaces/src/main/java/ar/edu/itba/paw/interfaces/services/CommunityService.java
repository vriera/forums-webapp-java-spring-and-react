package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Community;
import java.util.List;
import java.util.Optional;

public interface CommunityService {
    List<Community> list();

    Optional<Community> findById(Number id );
}
