package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.interfaces.services.AnswersService;
import ar.edu.itba.paw.interfaces.services.CommunityService;
import ar.edu.itba.paw.interfaces.services.QuestionService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.controller.Commons;
import ar.edu.itba.paw.webapp.controller.UserController;
import ar.edu.itba.paw.webapp.form.AnswersForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
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
    public boolean checkUserCanAccessToQuestion(Authentication authentication,  Long idQuestion ){
        final User user = commons.currentUser();
        if(user == null) return  false;
        Optional<Question> question = qs.findById(user,idQuestion);
        if (question.isPresent()) {
                Optional<AccessType> access = cs.getAccess(user.getId(), question.get().getCommunity().getId());
                return (access.isPresent() && access.get() == AccessType.ADMITTED) || question.get().getCommunity().getModerator().getId() == user.getId() || question.get().getCommunity().getModerator().getId() == 0 ;
        } return false; //the controller will respond 404
    }

    @Transactional(readOnly = true)
    public boolean checkUserCanAccessToCommunity(Authentication authentication, Long id, Long idCommunity ){
        if(checkUser(id)) {
            final User user = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).get();
            Optional<Community> community = cs.findById(idCommunity);
            if (community.isPresent()) {
                Optional<AccessType> access = cs.getAccess(user.getId(), community.get().getId());
                return (access.isPresent() && access.get() == AccessType.ADMITTED) || community.get().getModerator().getId() == user.getId() || community.get().getModerator().getId() == 0 ;
            }
            return true; //the controller will respond 404
        }else return false;
    }

    @Transactional(readOnly = true)
    public boolean  checkCanGetAnswers(Authentication authentication, HttpServletRequest request) {
        String userId = request.getParameter("userId");
        String questionId = request.getParameter("idQuestion");
        if(userId!=null){
            if(questionId!=null) return true; //bad request
            return checkUser(Long.valueOf(userId));
        }else if(questionId!=null) return checkCanAccessToQuestion(authentication,Long.valueOf(questionId));
        return true; //bad request
    }
    @Transactional(readOnly = true)
    public boolean  checkCanGetCommunities(Authentication authentication, HttpServletRequest request) {
        String userId = request.getParameter("userId");
        String moderatorId = request.getParameter("moderatorId");
        if(userId!=null){
            if(moderatorId!=null) return true; //bad request
            return checkUser(Long.valueOf(userId));
        }else if(moderatorId!=null) return checkUser(Long.valueOf(moderatorId));
        return true; //bad request
    }

    @Transactional(readOnly = true)
    public boolean     canChangeAccess(Authentication authentication, HttpServletRequest request, Long userId, Long communityId) {
        String moderatorId = request.getParameter("moderatorId");
        boolean checkModerator = checkUser(Long.valueOf(moderatorId));
        if(checkModerator && moderatorId!=null) return checkUserisModeratorOfTheCommunity(authentication,communityId);
        else return checkUser(Long.valueOf(userId));
    }




    @Transactional(readOnly = true)
    public boolean  checkQuestionOwner(Authentication authentication, Long id) {
        User user = commons.currentUser();
        Optional<Question> question = qs.findById(user, id);
        if (question.isPresent()) {
            return  question.get().getOwner().equals(user);

        }
        return true; // bad request
    }


    @Transactional(readOnly = true)
    public boolean checkUserisModeratorOfTheCommunity(Authentication authentication, Long idCommunity ){
        final User user = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).get();
        Optional<Community> community = cs.findById(idCommunity);
        if (community.isPresent()) {
            //Optional<AccessType> access = cs.getAccess(user.getId(), community.get().getId());
            return community.get().getModerator().getId() == user.getId();
        }
        return true; //the controller will respond 404
    }


    public boolean checkCanAccessToQuestionAnswerForm(Authentication authentication,  AnswersForm form){
        Long questionId = form.getQuestionId();
        return checkCanAccessToQuestion(authentication,questionId);
    }

    @Transactional(readOnly = true)
    public boolean checkCanAccessToQuestion(Authentication authentication, Long idQuestion ){
        if(idQuestion == null) return true; // return bad request
        final Optional<User> user =  us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if (user.isPresent()) {
            Optional<Question> question = qs.findById(user.get(), idQuestion);
            if (question.isPresent()) {
                AccessType access = null;
                boolean userMod = false;
                Optional<AccessType> a = cs.getAccess(user.get().getId(), question.get().getCommunity().getId());
                if (a.isPresent()) access = a.get();
                userMod = question.get().getCommunity().getModerator().getId() == user.get().getId();
                return (access != null && access == AccessType.ADMITTED || userMod || question.get().getCommunity().getModerator().getId() == 0);
            }
        }return  false;
    }


    public boolean checkUser(Long id) {
        final Optional<User> user = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        return user.isPresent() && user.get().getId() == id;
    }

    public boolean checkUserParam(HttpServletRequest request) {
        Long id = Long.valueOf(request.getParameter("requestorId"));
        final Optional<User> user = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if (!user.isPresent()) return false;
        return (id!=null && user.get().getId() == id);
    }


    @Transactional(readOnly = true)
    public boolean checkUserModerator(Authentication authentication, Long id) {
        final Optional<User> user = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if (!user.isPresent()) return false;
        Optional<Community> community = cs.findById(id);
        if(community.isPresent()){
            return community.get().getModerator().getId() == user.get().getId();
        }
        return true; //the controller will respond 404
    }

    @Transactional(readOnly = true)
    public boolean checkUserModeratorParam(HttpServletRequest request) {
        Long userId = Long.valueOf(request.getParameter("moderatorId"));
        Long id = Long.valueOf(request.getParameter("communityId"));
        if(checkUser(userId)) {
            final User user = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).get();
            Optional<Community> community = cs.findById(id);
            if (community.isPresent()) {
                Long communityModeratorId =community.get().getModerator().getId();
                return communityModeratorId == user.getId() || communityModeratorId == 0;
            }
            return true; //the controller will respond 404
        } return false;
    }

    @Transactional(readOnly = true)
    public boolean checkCanAccessToAnswer(Authentication authentication , Long id){
            Optional<Answer> answer = as.findById(id);
            if(answer.isPresent()){
                final Optional<User> user =  us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
                AccessType access = null;
                boolean userMod = false;
                if (user.isPresent()) {
                   Optional<AccessType> a = cs.getAccess(user.get().getId(), answer.get().getQuestion().getCommunity().getId());
                   if(a.isPresent()) access = a.get();
                   userMod = answer.get().getQuestion().getCommunity().getModerator().getId() == user.get().getId();

                }
                return (access!=null && access == AccessType.ADMITTED || userMod || answer.get().getQuestion().getCommunity().getModerator().getId() == 0 );
            } else return true; //the controller will respond 404

    }

    @Transactional(readOnly = true)
    public boolean checkCanAccessToQuestion(HttpServletRequest request ) {
        Long id = Long.valueOf(request.getParameter("idQuestion"));
        final Optional<User> user = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if (user.isPresent()) {
            Optional<Question> question = qs.findById(user.get(), id);
            if (question.isPresent()) {

                AccessType access = null;
                boolean userMod = false;

                Optional<AccessType> a = cs.getAccess(user.get().getId(), question.get().getCommunity().getId());
                if (a.isPresent()) access = a.get();
                userMod = question.get().getCommunity().getModerator().getId() == user.get().getId();
                return (access != null && access == AccessType.ADMITTED || userMod || question.get().getCommunity().getModerator().getId() == 0);
            }
        }
        return true;
    }


    @Transactional(readOnly = true)
    public boolean checkUserSameAsParam(HttpServletRequest request ){
        User u = commons.currentUser();
        Long userId = Long.valueOf(request.getParameter("userId")); //TODO: ta tirando null
        return !(u == null || u.getId() != userId);
    }
}
