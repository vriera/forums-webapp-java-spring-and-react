package ar.edu.itba.paw.webapp.auth.accessControl;


import ar.edu.itba.paw.interfaces.services.CommunityService;
import ar.edu.itba.paw.models.AccessType;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.controller.Commons;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.NoSuchElementException;
import java.util.Optional;

@Component
public class CommunityAccessControl {


    @Autowired
    private AccessControl ac;

    @Autowired
    private CommunityService cs;

    @Autowired
    private Commons commons;


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

}
