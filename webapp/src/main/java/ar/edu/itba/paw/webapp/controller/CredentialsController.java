package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.form.LoginForm;
import ar.edu.itba.paw.webapp.form.UserForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Optional;

@Controller
public class CredentialsController {
    @Autowired
    UserService us;

    @Autowired
    AuthenticationManager authenticationManager;

    @RequestMapping(path="/credentials/login", method = RequestMethod.GET)
    public ModelAndView loginGet(@ModelAttribute("loginForm") LoginForm loginForm){
        ModelAndView mav = new ModelAndView("credentials/login");


        return mav;
    }

    @RequestMapping(path="/credentials/login", method = RequestMethod.POST)
    public ModelAndView loginPost(@ModelAttribute("loginForm") @Valid LoginForm loginForm, BindingResult errors){
        if(errors.hasErrors()){
            return loginGet(loginForm);
        }

        return new ModelAndView("redirect:/");
    }

    @RequestMapping(path="/credentials/register", method = RequestMethod.GET)
    public ModelAndView registerGet(@ModelAttribute("userForm")UserForm userForm, boolean emailUsed){
        ModelAndView mav = new ModelAndView("credentials/register");

        mav.addObject("emailUsed", emailUsed); //TODO: usarlo para manejo de errores

        return mav;
    }

    @RequestMapping(path="/credentials/register", method = RequestMethod.POST)
    public ModelAndView registerPost(@ModelAttribute("userForm") @Valid UserForm userForm, BindingResult errors){
        ModelAndView mav = new ModelAndView("credentials/register");

        if (errors.hasErrors() || !userForm.getPassword().equals(userForm.getRepeatPassword())) {
            return registerGet(userForm , false);
        }

        final Optional<User> u = us.create(userForm.getUsername(), userForm.getEmail(), userForm.getPassword());

        if(!u.isPresent()) //La única razón de falla es si el mail está tomado
            return registerGet(userForm , true);

        System.out.println("Inicio sesión");
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
        System.out.println("User email: "+sc.getAuthentication().getName());
    }


    @RequestMapping("/user/{id}/verify/")
    public ModelAndView verifyEmail(@PathVariable("id") long id){

        Optional<User> user = us.verify(id);
        String redirect = String.format("/");
        return new ModelAndView(redirect);
    }





}
