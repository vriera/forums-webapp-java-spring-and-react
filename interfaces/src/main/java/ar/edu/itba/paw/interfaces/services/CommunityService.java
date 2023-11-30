package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.AccessType;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.CommunityNotifications;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface CommunityService {
    Community findByName(String name);
    //Lista las comunidades a las que el usuario tiene acceso
    List<Community> list(User requester);

    //Busca entre las comunidades sin importar si el usuario tiene acceso o no
    Community findById(Number id );

    Community create(String title, String description, User moderator) throws IllegalArgumentException;

    //Devuelve los usuarios miembros de la comunidad
    List<User> getMembersByAccessType(Number communityId, AccessType type, Number page);

    List<Community> getPublicCommunities();

    //Devuelve el tipo de acceso del usuario
    Optional<AccessType> getAccess(Number userId, Number communityId);

    //Chequea que el usuario pueda acceder a la comunidad
    boolean canAccess(User user, Community community);

    boolean isModerator(User user, Community community);

    // Returns the number of pages needed to display the data
    long getMembersByAccessTypePages(Number communityId, AccessType type);

    // Perform the action needed to get the user to the target access type
    void modifyAccessType(long userId, long communityId, AccessType targetAccessType);

    // The user requests the moderator to allow him to access the community
    boolean requestAccess(Number userId, Number communityId);

    // The moderator admits the user to the community by accepting the request
    boolean acceptRequest(Number userId, Number communityId, Number authorizerId);

    // The moderator rejects the user's request to access the community
    boolean rejectRequest(Number userId, Number communityId, Number authorizerId);

    // The moderator invites the user to the community, but the admission is pending
    boolean invite(Number userId, Number communityId, Number authorizerId);

    // The user accepts an invitation to the community
    boolean acceptInvite(Number userId, Number communityId);

    // The user rejects an invitation to the community
    boolean rejectInvite(Number userId, Number communityId);

    // The moderator kicks the user out of the community
    boolean kick(Number userId, Number communityId, Number authorizerId);

    // The moderator bans the user from the community
    boolean ban(Number userId, Number communityId, Number authorizerId);

    // The moderator lifts the ban on the user
    boolean liftBan(Number userId, Number communityId, Number authorizerId);

    // The user leaves the community
    boolean leave(Number userId, Number communityId);

    // The user leaves the community, and cannot be invited again
    boolean block(Number userId, Number communityId);

    // The user, after leaving the community, allows himself to be invited again
    boolean unblock(Number userId, Number communityId);

    List<CommunityNotifications> getCommunityNotifications(Number authorizerId);

    CommunityNotifications getCommunityNotificationsById(Number communityId);

    Number getUserCount(Number communityId);

    List<Community>  list(Number userId , Number limit  , Number offset);
    long listCount(Number userdId);


}
