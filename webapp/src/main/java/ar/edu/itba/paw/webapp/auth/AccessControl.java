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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Null;
import java.util.Optional;



@Component
public class AccessControl {

    @Autowired
    private CommunityDao cs; //TODO: VOLVER A MANDAR UN MAIL RECONFIRMANDO QUE ESTA BIEN QUE LE PREGUNTE A LOS DAOS Y NO A LOS SERIVICES

    @Autowired
    private QuestionDao qs;

    @Autowired
    private UserDao us;

    @Autowired
    private AnswersDao as;

    @Transactional(readOnly = true)
    public boolean checkUserCanAccessToQuestion(Authentication authentication, Long id, Long idQuestion ){
        final User user = checkUser(id);
        if (user == null) return false;
        Optional<Question> question = qs.findById(idQuestion);
        if(question.isPresent()){
            Optional<AccessType> access = cs.getAccess(user.getId(),question.get().getCommunity().getId());
            return access.isPresent() && access.get() == AccessType.ADMITTED;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public boolean checkCanAccessToQuestion(Authentication authentication, Long idQuestion ){
        final Optional<User> user =  us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        System.out.println("HOLA: " + user.isPresent());
        if (!user.isPresent()) return false;
        Optional<Question> question = qs.findById(idQuestion);
        if(question.isPresent()){
            Optional<AccessType> access = cs.getAccess(user.get().getId(),question.get().getCommunity().getId());
            return access.isPresent() && access.get() == AccessType.ADMITTED;
        }
        return false;
    }

    public User checkUser(Long id) {
        final Optional<User> user = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if (!user.isPresent()) return null;
        if(user.get().getId() == id) return user.get();
        return null;
    }


    @Transactional(readOnly = true)
    public boolean checkCanAccessToAnswer(Authentication authentication, Long id){
        final Optional<User> user =  us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if (!user.isPresent()) return false;
        Optional<Answer> answer = as.findById(id);
        if(answer.isPresent()){
            Optional<AccessType> access = cs.getAccess(user.get().getId(),answer.get().getQuestion().getCommunity().getId());
            return access.isPresent() && access.get() == AccessType.ADMITTED;
        }
        return false;
    }
}
