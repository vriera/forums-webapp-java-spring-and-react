package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HelloWorldController {
    @Autowired
    UserService userService;

    @RequestMapping("/")
    public ModelAndView helloWorld() {
        final ModelAndView mav = new ModelAndView("index");
        mav.addObject("greeting", userService.list().get(0).getName());
        return mav;
    }

    @RequestMapping("/users")
    public ModelAndView users(@RequestParam("id") String id){
        final ModelAndView mav = new ModelAndView("index");
        mav.addObject("greeting", userService.findById(id).getName());
        return mav;
    }
}