package ar.edu.itba.paw.webapp.controller.utils;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.auth.PawUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

public class AuthenticationUtils {

    private AuthenticationUtils(){}


    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationUtils.class);


    public static Optional<User> authorizeInView(ModelAndView mav, UserService us){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> auxuser = us.findByEmail(auth.getName());
        Boolean user = auxuser.isPresent();
        mav.addObject("is_user_present", user);
        if(user){
            LOGGER.debug("Logged user: id={}, email={}", auxuser.get().getId(), auxuser.get().getEmail());
            mav.addObject("user", auxuser.get());
            mav.addObject("notifications" , us.getNotifications(auxuser.get().getId()).get());
        }
        return auxuser;
    }

    //Manualmente inyecta la sesión correspondiente al email
    public static void authenticate(String email, PawUserDetailsService userDetailsService){
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        Authentication auth = new UsernamePasswordAuthenticationToken(email, null, userDetails.getAuthorities());
        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(auth);
        LOGGER.debug("Session created successfully for: {}", email);
    }
}
