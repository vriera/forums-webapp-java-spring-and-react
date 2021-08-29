package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
public class HelloWorldController {
    @Autowired
    UserService us;

    @RequestMapping("/")
    public ModelAndView index() {
        final ModelAndView mav = new ModelAndView("landing");
        return mav;
    }

    @RequestMapping("/create")
    public ModelAndView create(@RequestParam(value = "name", required = true) final String username, @RequestParam(value="password") final String password) {
        final Optional<User> u = us.create(username, password);
        return new ModelAndView("redirect:/?userId=" + (u.isPresent()? u.get().getUserid() : -1));
    }

    @RequestMapping("/ask/pick_community")
    public ModelAndView pick_community(){
        ModelAndView mav = new ModelAndView("ask/pick_community");

        String[] dummy_list = {"Matemática", "Filosofía", "Psicología", "Derecho", "Programación", "Ocultismo", "Magia negra", "Cocina"};
        List<String> community_list = new ArrayList<>();
        Collections.addAll(community_list, dummy_list);

        community_list.stream().forEach(System.out::println);

        mav.addObject("community_list", community_list);

        return mav;
    }


}