package ar.edu.itba.paw.webapp.auth.access;

import ar.edu.itba.paw.interfaces.services.CommunityService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.AccessType;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.auth.access.utils.AccessControlUtils;
import ar.edu.itba.paw.webapp.controller.utils.Commons;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
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

    @Transactional(readOnly = true)
    public boolean canAccess(User user, long communityId) {
        try {
            Community community = cs.findById(communityId);
            AccessType access = AccessType.NONE;
            boolean userIsMod = false;
            if (user != null) {

                access = cs.getAccess(user.getId(), community.getId());
                userIsMod = user.getId() == community.getModerator().getId();
            }
            boolean userIsAdmitted = access.equals(AccessType.ADMITTED);
            boolean communityIsPublic = community.getModerator().getId() == 0;
            return communityIsPublic || userIsMod || userIsAdmitted;
        } catch (NoSuchElementException e) {
            // si hay un 404 lo dejo pasar para que se encargue en controller de tirarlo
            return true;
        }
    }

    @Transactional(readOnly = true)
    public boolean canCurrentUserModerate(long communityId) {
        return canModerate(commons.currentUser(), communityId);
    }

    @Transactional(readOnly = true)
    public boolean canUserModerate(long userId, long communityId) {
        try {
            User user = us.findById(userId);
            return canModerate(user, communityId);
        } catch (NoSuchElementException e) {
            return true;
        }
    }

    @Transactional(readOnly = true)
    private boolean canModerate(User user, long communityId) {
        if (user == null)
            return false;
        try {
            Community community = cs.findById(communityId);
            return community.getModerator().getId() == user.getId();
        } catch (NoSuchElementException e) {
            // si hay un 404 lo dejo pasar para que se encargue en controller de tirarlo
            return true;
        }
    }

    @Transactional(readOnly = true)
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

    private boolean canModeratorPerformAccessTypeModification(long userId, long communityId,
            AccessType targetAccessType) {
        Set<AccessType> accessTypesExclusivelyAccessibleToModerator = Collections.unmodifiableSet(
                new HashSet<>(Arrays.asList(AccessType.BANNED, AccessType.KICKED, AccessType.INVITED,
                        AccessType.REQUEST_REJECTED)));
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
