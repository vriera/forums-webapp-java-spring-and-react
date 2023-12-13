package ar.edu.itba.paw.webapp.controller.utils;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.NoSuchElementException;
import java.util.Optional;

@ControllerAdvice
public class Commons {

    @Autowired
    private UserService us;

    @ModelAttribute("currentUser")
    public User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        try {
            return us.findByEmail(auth.getName());
        } catch (NoSuchElementException e) {
            return null;
        }
    }
}
