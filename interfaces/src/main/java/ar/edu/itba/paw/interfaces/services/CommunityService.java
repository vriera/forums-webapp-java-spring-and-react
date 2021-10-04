package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.AccessType;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.User;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface CommunityService {
    List<Community> list();

    Optional<Community> findById(Number id );

    Optional<Community> create(String title, String description, User moderator);

    //Devuelve los usuarios miembros de la comunidad
    List<User> getMembersByAccessType(Number communityId, AccessType type);

    //El usuario peticiona que el moderador le permita acceso a la comunidad
    boolean requestAccess(Number userId, Number communityId);

    //El moderador admite al usuario en la comunidad
    boolean admitAccess(Number userId, Number communityId, User authorizer);

    //El moderador rechaza al usuario en la comunidad
    boolean rejectAccess(Number userId, Number communityId, User authorizer);

    //Invita al usuario a la comunidad, pero la membresía está pendiente
    boolean invite(Number userId, Number communityId);

    //El usuario acepta una invitación a la comunidad
    boolean acceptInvite(Number userId, Number communityId);

    //El usuario rechaza una invitación a la comunidad
    boolean refuseInvite(Number userId, Number communityId);

    //El moderador echa al usuario de la comunidad si estaba invitado
    boolean kick(Number userId, Number communityId, User authorizer);

    //El moderador proscribe al usuario de la comunidad si estaba invitado
    boolean ban(Number userId, Number communityId, User authorizer);

    //El moderador vuelve a admitir al usuario de la comunidad si estaba invitado
    boolean liftBan(Number userId, Number communityId, User authorizer);

    //El usuario deja la comunidad
    boolean leaveCommunity(Number userId, Number communityId);

    //El usuario abandona la comunidad, y no se le puede volver a invitar
    boolean blockCommunity(Number userId, Number communityId);

    //El usuario, luego de abandonar la comunidad, permite que lo vuelvan a invitar
    boolean unblockCommunity(Number userId, Number communityId);
}
