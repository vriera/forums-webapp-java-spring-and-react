package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.controller.utils.AuthenticationUtils;
import ar.edu.itba.paw.webapp.form.LoginForm;
import ar.edu.itba.paw.webapp.form.UserForm;
import jdk.nashorn.internal.lookup.MethodHandleFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Optional;

@Controller
public class CredentialsController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationUtils.class);

    @Autowired
    UserService us;

    @Autowired
    AuthenticationManager authenticationManager;

    @RequestMapping(path="/credentials/login", method = RequestMethod.GET)
    public ModelAndView loginGet(@ModelAttribute("loginForm") LoginForm loginForm, Boolean invalidEmail, @RequestParam(value = "error", required = false, defaultValue = "false") Boolean error){
        ModelAndView mav = new ModelAndView("credentials/login");
        AuthenticationUtils.authorizeInView(mav, us);

        mav.addObject("invalidEmail", invalidEmail);
        mav.addObject("loginError", error);
        return mav;
    }

    @RequestMapping(path="/credentials/login", method = RequestMethod.POST)
    public ModelAndView loginPost(@ModelAttribute("loginForm") @Valid LoginForm loginForm, BindingResult errors){
        boolean validEmail = us.findByEmail(loginForm.getEmail()).isPresent();
        if(!validEmail){
            LOGGER.debug("El email es invalido");
            return loginGet(loginForm, true, false);
        }
        if(errors.hasErrors() ){
            return loginGet(loginForm, false, false);
        }

        return new ModelAndView("redirect:/");
    }

    @RequestMapping(path="/credentials/register", method = RequestMethod.GET)
    public ModelAndView registerGet(@ModelAttribute("userForm")UserForm userForm, boolean emailUsed, boolean differentPasswords){
        ModelAndView mav = new ModelAndView("credentials/register");
        AuthenticationUtils.authorizeInView(mav, us);

        mav.addObject("emailUsed", emailUsed);
        mav.addObject("differentPasswords", differentPasswords);

        return mav;
    }

    @RequestMapping(path="/credentials/register", method = RequestMethod.POST)
    public ModelAndView registerPost(@ModelAttribute("userForm") @Valid UserForm userForm, BindingResult errors){
        ModelAndView mav = new ModelAndView("credentials/register");
        AuthenticationUtils.authorizeInView(mav, us);

        if (errors.hasErrors() || !userForm.getPassword().equals(userForm.getRepeatPassword())) {
            return registerGet(userForm , false, true );
        }

        final Optional<User> u = us.create(userForm.getUsername(), userForm.getEmail(), userForm.getPassword());

        if(!u.isPresent()) //La única razón de falla es si el mail está tomado
            return registerGet(userForm , true, false);

        LOGGER.debug("Sesion created successfully");
        login(userForm.getEmail(), userForm.getPassword());
        return new ModelAndView("redirect:/");
    }

    //Manualmente inyecta la sesión correspondiente
    private void login(String email, String password){
        UsernamePasswordAuthenticationToken authReq
                = new UsernamePasswordAuthenticationToken(email, password);
        Authentication auth = authenticationManager.authenticate(authReq);
        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(auth);
    }


    @RequestMapping("/user/{id}/verify/")
    public ModelAndView verifyEmail(@PathVariable("id") long id){

        Optional<User> user = us.verify(id);
        String redirect = String.format("/");
        ModelAndView mav = new ModelAndView(redirect);
        AuthenticationUtils.authorizeInView(mav, us);
        return mav;
    }





}
