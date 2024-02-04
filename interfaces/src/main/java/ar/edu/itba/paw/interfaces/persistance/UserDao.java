package ar.edu.itba.paw.interfaces.persistance;

import ar.edu.itba.paw.models.AccessType;
import ar.edu.itba.paw.models.Karma;
import ar.edu.itba.paw.models.Notification;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    List<User> list();

    Optional<User> findByEmail(String email);

    Optional<User> findById(long id);

    User create(String username, String email, String password);

    Optional<User> updateCredentials(User user, String newUsername, String newPassword);

    //Devuelve los usuarios con acceso a la comunidad dado un tipo de acceso
    List<User> getMembersByAccessType(Number communityId, AccessType type, int page, int limit);

    //Devuelve las páginas que se van a necesitar para plasmar los datos
    long getMemberByAccessTypeCount(Long communityId, AccessType type);

    Optional<Notification> getNotifications(Number userId);


    Optional<Karma> getKarma(Number userId);

    List<User> getUsers(int page);
}
