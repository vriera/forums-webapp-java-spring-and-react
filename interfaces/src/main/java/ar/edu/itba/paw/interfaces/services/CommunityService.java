package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.User;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface CommunityService {
    List<Community> list();

    Optional<Community> findById(Number id );

    Optional<Community> create(String title, String description, User moderator);
}
