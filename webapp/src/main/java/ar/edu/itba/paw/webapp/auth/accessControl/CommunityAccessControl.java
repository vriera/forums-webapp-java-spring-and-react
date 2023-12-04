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

    public boolean checkUserCanModifyAccess(long targetUserId, long communityId, HttpServletRequest request) throws IOException {
        JSONObject body = AccessControlUtils.extractBodyAsJson(request);
        String targetAccessTypeString = body.getString("accessType");

        LOGGER.debug("Checking if user can modify access: targetUserId={}, communityId={}, accessType={}",
                targetUserId, communityId, targetAccessTypeString);

        AccessType targetAccessType;
        try {
            targetAccessType = AccessType.valueOf(targetAccessTypeString);
        } catch (IllegalArgumentException e) {
            return true; // This is a bad request, it should be dealt with in the controller
        }

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
            return cs.canModeratorPerformAccessTypeModification(targetUserId, communityId, targetAccessType);
        }

        return !cs.canModeratorPerformAccessTypeModification(targetUserId, communityId, targetAccessType);
    }

}
