package ar.edu.itba.paw.webapp.auth.accessControl;


import ar.edu.itba.paw.interfaces.services.CommunityService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.AccessType;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.controller.Commons;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Component
public class CommunityAccessControl {


    @Autowired
    private AccessControl ac;

    @Autowired
    private CommunityService cs;

    @Autowired
    private Commons commons;

    @Autowired
    private UserService us;

private static final Set<AccessType> accessTypesExclusivelyAccessibleToModerator = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList(AccessType.ADMITTED, AccessType.INVITED, AccessType.REQUESTED)));

    public boolean canAccess(long userId , long communityId)  throws NoSuchElementException {
        return canAccess( ac.checkUser(userId), communityId);
    }


    public boolean canAccess( long communityId)  throws NoSuchElementException {
        return canAccess( commons.currentUser(), communityId);
    }

    public boolean canAccess(User user , long communityId)  throws NoSuchElementException {


        Community community = cs.findById(communityId);


        Optional<AccessType> access =Optional.empty();
        boolean userIsMod = false;
        if(user !=null) {
             access = cs.getAccess(user.getId(), community.getId());
             userIsMod =user.getId() == community.getModerator().getId();
        }
        boolean userIsAdmitted = access.isPresent() && access.get().equals(AccessType.ADMITTED);
        boolean communityIsPublic = community.getModerator().getId() == 0;
        return communityIsPublic || userIsMod || userIsAdmitted;
    }

    public boolean canCurrentUserModerate(long communityId)  throws NoSuchElementException{
        return canModerate(commons.currentUser(),communityId);
    }
    public boolean canModerate(long userId, long communityId){
        User user = ac.checkUser(userId);
        return canModerate(user,communityId);
    }

    private boolean canModerate(User user , long communityId)  throws NoSuchElementException{
        if(user == null )
            return false;
       Community community = cs.findById(communityId);
        return  community.getModerator().getId() == user.getId();
    }

    private boolean invalidCredentials(long targetUserId, long communityId, AccessType targetAccessType) {
        User currentUser = commons.currentUser();
        if(currentUser == null) {
            return false;
        }

        User targetUser = us.findById(targetUserId);

        Community community = cs.findById(communityId);

        // Target user cannot be the moderator, the service should throw an exception
        if ( targetUser.equals(community.getModerator())
                && currentUser.equals(community.getModerator()) ) {
            return true;
        }

        // If the authorizer is the moderator, they can only transition to a type that is exclusively accessible to the moderator or ADMITTED
        if ( currentUser.equals(community.getModerator()) ) {
            return accessTypesExclusivelyAccessibleToModerator.contains(targetAccessType) || targetAccessType.equals(AccessType.ADMITTED);
        }

        // If the target user is not the moderator of the community, they cannot perform transitions exclusively accessible to the moderator
        return !accessTypesExclusivelyAccessibleToModerator.contains(targetAccessType);
    }

}
