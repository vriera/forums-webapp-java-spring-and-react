package ar.edu.itba.paw.webapp.auth.accessControl;

import ar.edu.itba.paw.interfaces.services.CommunityService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.AccessType;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.auth.accessControl.utils.AccessControlUtils;
import ar.edu.itba.paw.webapp.controller.Commons;
import javassist.NotFoundException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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
            new HashSet<>(Arrays.asList(AccessType.BANNED, AccessType.KICKED, AccessType.INVITED, AccessType.REQUEST_REJECTED)));
    private static final Logger LOGGER = LoggerFactory.getLogger(CommunityAccessControl.class);
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

    public boolean checkUserCanModifyAccess(long targetUserId, long communityId, HttpServletRequest request) throws IOException {
        JSONObject body = AccessControlUtils.extractBodyAsJson(request);
        String targetAccessTypeString = body.getString("accessType");

        LOGGER.debug("Checking if user can modify access: targetUserId={}, communityId={}, accessType={}",
                targetUserId, communityId, targetAccessTypeString);

        AccessType targetAccessType;
        try {
            targetAccessType = AccessType.valueOf(targetAccessTypeString);
        } catch (IllegalArgumentException e) {
            LOGGER.error("Access type not found: {}", targetAccessTypeString);
            return false;
        }

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
