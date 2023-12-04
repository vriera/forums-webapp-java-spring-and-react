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

    public boolean canAccess(long userId , long communityId) {
        return canAccess( ac.checkUser(userId), communityId);
    }
    public boolean canAccess( long communityId)  {
        return canAccess( commons.currentUser(), communityId);
    }

    public boolean canAccess(User user , long communityId){
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
        }catch (NoSuchElementException e ){
            // si hay un 404 lo dejo pasar para que se encargue en controller de tirarlo
            return true;
        }
    }

    public boolean canCurrentUserModerate(long communityId){
        return canModerate(commons.currentUser(),communityId);
    }

    private boolean canModerate(User user , long communityId) {
        if(user == null )
            return false;
        try {
            Community community = cs.findById(communityId);
            return  community.getModerator().getId() == user.getId();
        }catch (NoSuchElementException e ){
            // si hay un 404 lo dejo pasar para que se encargue en controller de tirarlo
            return true;
        }

    }
}
