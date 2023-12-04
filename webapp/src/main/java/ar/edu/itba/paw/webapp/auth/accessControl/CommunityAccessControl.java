package ar.edu.itba.paw.webapp.auth.accessControl;

import ar.edu.itba.paw.interfaces.services.CommunityService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.AccessType;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.auth.accessControl.utils.AccessControlUtils;
import ar.edu.itba.paw.webapp.controller.Commons;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(CommunityAccessControl.class);
    @Autowired
    private AccessControl ac;
    @Autowired
    private CommunityService cs;
    @Autowired
    private Commons commons;
    @Autowired
    private UserService us;

    public boolean canAccess(User user, long communityId) throws NoSuchElementException {
        Community community = cs.findById(communityId);

        return cs.canAccess(user, community);
    }

    public boolean canCurrentUserModerate(long communityId) throws NoSuchElementException {
        return canModerate(commons.currentUser(), communityId);
    }

    public boolean canModerate(long userId, long communityId) {
        User user = ac.checkUser(userId);
        return canModerate(user, communityId);
    }

    private boolean canModerate(User user, long communityId) throws NoSuchElementException {
        if (user == null)
            return false;
        Community community = cs.findById(communityId);
        return community.getModerator().getId() == user.getId();
    }

    public boolean checkUserCanModifyAccess(long targetUserId, long communityId, HttpServletRequest request) {
        try {
            JSONObject body = AccessControlUtils.extractBodyAsJson(request);
            String targetAccessTypeString = body.getString("accessType");

            LOGGER.debug("Checking if user can modify access: targetUserId={}, communityId={}, accessType={}",
                    targetUserId, communityId, targetAccessTypeString);

            AccessType targetAccessType = AccessType.valueOf(targetAccessTypeString);


        // These operations are only available to logged users
        User currentUser = commons.currentUser();
        if (currentUser == null) {
            return false;
        }

        User targetUser = us.findById(targetUserId);
        Community community = cs.findById(communityId);

        // Target user cannot be the moderator, the service should throw an exception
        if (targetUser.equals(community.getModerator())
                && currentUser.equals(community.getModerator())) {
            return true;
        }

        if (currentUser.equals(community.getModerator())) {
            return canModeratorPerformAccessTypeModification(targetUserId, communityId, targetAccessType);
        }

        return !canModeratorPerformAccessTypeModification(targetUserId, communityId, targetAccessType);
        } catch (Exception e) {
            return true; // This is a bad request or not found
        }
    }

    private boolean canModeratorPerformAccessTypeModification(long userId, long communityId, AccessType targetAccessType) {
        Set<AccessType> accessTypesExclusivelyAccessibleToModerator = Collections.unmodifiableSet(
                new HashSet<>(Arrays.asList(AccessType.BANNED, AccessType.KICKED, AccessType.INVITED, AccessType.REQUEST_REJECTED)));
        boolean canPerform = false;

        if (accessTypesExclusivelyAccessibleToModerator.contains(targetAccessType)) {
            canPerform = true;
        }
        // The moderator is accepting a request
        else if (targetAccessType.equals(AccessType.ADMITTED)) {
            canPerform = cs.getAccess(userId, communityId).equals(AccessType.REQUESTED);
        }
        // The moderator is lifting a ban
        else if (targetAccessType.equals(AccessType.NONE)) {
            canPerform = cs.getAccess(userId, communityId).equals(AccessType.BANNED);
        }

        return canPerform;
    }
}
