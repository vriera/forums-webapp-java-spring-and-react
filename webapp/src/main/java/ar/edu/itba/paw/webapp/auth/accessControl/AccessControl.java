package ar.edu.itba.paw.webapp.auth.accessControl;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.controller.utils.Commons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

@Component
public class AccessControl {

    @Autowired
    private Commons commons;

    @Transactional(readOnly = true)
    public boolean isLoggedUser(Long id){
        return getUserIfIsRequester(id) != null;
    }

    @Transactional(readOnly = true)
    public User getUserIfIsRequester(Long id) {
        final User u = commons.currentUser();
        if(u == null)
            return null;

        if(u.getId() != id)
            return null;
        return u;
    }

    @Transactional(readOnly = true)
    public boolean checkUserParam(HttpServletRequest request) {
        Long id = Long.valueOf(request.getParameter("userId"));
        return getUserIfIsRequester(id) != null ;
    }

    @Transactional(readOnly = true)
    public boolean checkUserOrPublicParam(HttpServletRequest request) {
        long id = Long.parseLong(request.getParameter("userId"));
        return id == -1 || getUserIfIsRequester(id) != null;
    }

    @Transactional(readOnly = true)
    public boolean checkUserSameAsParam(HttpServletRequest request ){
        User u = commons.currentUser();
        long userId = Long.parseLong(request.getParameter("userId"));
        return u != null && u.getId() == userId;
    }
}
