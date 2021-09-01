package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
public class HelloWorldController {
    @Autowired
    UserService us;

    @RequestMapping("/")
    public ModelAndView index(@RequestParam(value = "userId", required = true) final int id) {
        final ModelAndView mav = new ModelAndView("index");
        final Optional<User> u = us.findById(id);
        mav.addObject("user", u.get());
        return mav;
    }

    @RequestMapping("/create")
    public ModelAndView create(@RequestParam(value = "name", required = true) final String username, @RequestParam(value="password") final String password) {
        final Optional<User> u = us.create(username, password);
        return new ModelAndView("redirect:/?userId=" + (u.isPresent()? u.get().getUserid() : -1));
    }

    @RequestMapping("/community")
    public ModelAndView community(){
        return new ModelAndView("community");
    }
}