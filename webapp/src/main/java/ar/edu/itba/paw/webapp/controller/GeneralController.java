package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
public class GeneralController {
    @Autowired
    UserService us;

    @RequestMapping("/")
    public ModelAndView index() {
        final ModelAndView mav = new ModelAndView("landing");

        String[] dummy_list = {"Matemática", "Filosofía", "Psicología", "Derecho", "Programación", "Ocultismo", "Magia negra", "Cocina"};
        List<String> community_list = new ArrayList<>();
        Collections.addAll(community_list, dummy_list);

        mav.addObject("community_list", community_list);

        return mav;
    }

    @RequestMapping("/ask/community")
    public ModelAndView pickCommunity(){
        ModelAndView mav = new ModelAndView("ask/community");

        String[] dummyList = {"Matemática", "Filosofía", "Psicología", "Derecho", "Programación", "Ocultismo", "Magia negra", "Cocina"};
        List<String> communityList = new ArrayList<>();
        Collections.addAll(communityList, dummyList);

        mav.addObject("communityList", communityList);

        return mav;
    }

    @RequestMapping("/ask/question")
    public ModelAndView createQuestion(){
        ModelAndView mav = new ModelAndView("ask/question");

        String[] dummyList = {"Foro 1", "Foro 2", "Foro 3", "Foro 4"};
        List<String> forumList = new ArrayList<>();
        Collections.addAll(forumList, dummyList);

        mav.addObject("forumList", forumList);

        return mav;
    }

    @RequestMapping("/ask/contact")
    public ModelAndView setContact(){
        ModelAndView mav = new ModelAndView("ask/contact");

        return mav;
    }

    @RequestMapping("/ask/finish")
    public ModelAndView uploadQuestion(){
        ModelAndView mav = new ModelAndView("ask/finish");

        Boolean success = true;
        mav.addObject("success", success);

        return  mav;
    }
}