package ar.edu.itba.paw.webapp.controller.utils;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

public abstract class AuthenticationUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationUtils.class);
    public static void authorizeInView(ModelAndView mav, UserService us){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> auxuser = us.findByEmail(auth.getName());
        Boolean user = auxuser.isPresent();
        mav.addObject("is_user_present", user);
        if(user){
            LOGGER.debug("Logged user: id={}, email={}", auxuser.get().getId(), auxuser.get().getEmail());
            mav.addObject("user", auxuser.get());
        }
    }
}
