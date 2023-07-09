package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.interfaces.persistance.AnswersDao;
import ar.edu.itba.paw.interfaces.persistance.CommunityDao;
import ar.edu.itba.paw.interfaces.persistance.QuestionDao;
import ar.edu.itba.paw.interfaces.persistance.UserDao;
import ar.edu.itba.paw.interfaces.services.AnswersService;
import ar.edu.itba.paw.interfaces.services.CommunityService;
import ar.edu.itba.paw.interfaces.services.QuestionService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.controller.Commons;
import ar.edu.itba.paw.webapp.controller.UserController;
import ar.edu.itba.paw.webapp.controller.utils.GenericResponses;
import javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Null;
import java.util.NoSuchElementException;
import java.util.Optional;



@Component
public class AccessControl {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);


    @Autowired
    private CommunityService cs; //TODO: VOLVER A MANDAR UN MAIL RECONFIRMANDO QUE ESTA BIEN QUE LE PREGUNTE A LOS DAOS Y NO A LOS SERIVICES

    @Autowired
    private QuestionService qs;

    @Autowired
    private UserService us;

    @Autowired
    private AnswersService as;

    @Autowired
    private Commons commons;

    @Transactional(readOnly = true)
    public boolean checkUserCanAccessToQuestion(Authentication authentication, Long userId, Long questionid ){
        User u = checkUser(userId);
        return u != null && qs.canAccess(u , questionid);
    }

    @Transactional(readOnly = true)
    public boolean checkUserCanAccessToCommunity(Authentication authentication, Long userId, Long communityId ){
       User u = checkUser(userId);
       return u!= null && cs.canAccess(u , communityId);
    }


    @Transactional(readOnly = true)
    public boolean checkCanAccessToQuestion(Authentication authentication, Long questionId ){
        return qs.canAccess(commons.currentUser(),questionId);
    }

    public boolean checkUserEqual(Long id){
        return checkUser(id) != null;
    }

    private User checkUser(Long id) {
        final User u = commons.currentUser();
        if(u == null)
            return null;

        if(u.getId() != id)
            return null;
        return u;
    }

    public boolean checkUserParam(HttpServletRequest request) {
        Long id = Long.valueOf(request.getParameter("requestorId"));
        return checkUser(id) != null;
    }


    @Transactional(readOnly = true)
    public boolean checkUserModerator(Authentication authentication, Long communityId) {
       return cs.isModerator(commons.currentUser() , communityId);
    }

    @Transactional(readOnly = true)
    public boolean checkUserModeratorParam(HttpServletRequest request) {
        Long userId = Long.valueOf(request.getParameter("moderatorId"));
        Long communityId = Long.valueOf(request.getParameter("communityId"));
        User user = checkUser(userId);
        return cs.isModerator(user , communityId);
    }

    @Transactional(readOnly = true)
    public boolean checkCanAccessToAnswer(Authentication authentication , Long answerId){
        return as.canAccess(commons.currentUser() , answerId);
    }

    @Transactional(readOnly = true)
    public boolean checkCanAccessToQuestion(HttpServletRequest request ){
        Long questionId = Long.valueOf(request.getParameter("idQuestion"));
        return qs.canAccess(commons.currentUser() , questionId);
    }


    @Transactional(readOnly = true)
    public boolean checkUserSameAsParam(HttpServletRequest request ){
        User u = commons.currentUser();
        long userId = Long.parseLong(request.getParameter("userId"));
        return u != null && u.getId() == userId;
    }


    @Transactional(readOnly = true)
        public boolean checkUserQuestionOwner( Long id) throws NotFoundException {
            User u = commons.currentUser();
            Optional<Answer> a = as.findById(id);

            if(!a.isPresent())
                throw new NotFoundException("");

            Optional<Question> q = qs.findById(u , a.get().getId());
            if(!q.isPresent())
                throw new NotFoundException("");

            if (u == null || u.getId() != q.get().getOwner().getId()) {
                return false;
            }
            return true;
    }

}
