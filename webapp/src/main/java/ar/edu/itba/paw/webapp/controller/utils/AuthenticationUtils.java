package ar.edu.itba.paw.webapp.controller.utils;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

public abstract class AuthenticationUtils {

    public static void authorizeInView(ModelAndView mav, UserService us){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> auxuser = us.findByEmail(auth.getName());
        Boolean user = auxuser.isPresent();
        mav.addObject("is_user_present", user);
        if(user){
            mav.addObject("user", auxuser.get());
        }
    }
}
