package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CommunityService;
import ar.edu.itba.paw.interfaces.services.ForumService;
import ar.edu.itba.paw.interfaces.services.QuestionService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Community;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
public class GeneralController {
    @Autowired
    UserService us;
    @Autowired
    private QuestionService qs;
    @Autowired
    private CommunityService cs;
    @Autowired
    private ForumService fs;
    @RequestMapping("/")
    public ModelAndView index() {
        final ModelAndView mav = new ModelAndView("landing");

        mav.addObject("community_list", cs.list());

        return mav;
    }

    @RequestMapping("/ask/community")
    public ModelAndView pickCommunity(){
        ModelAndView mav = new ModelAndView("ask/community");

        mav.addObject("communityList", cs.list());

        return mav;
    }

    @RequestMapping("/ask/question")
    public ModelAndView createQuestion(@RequestParam("communityId") Number id){
        ModelAndView mav = new ModelAndView("ask/question");

        Community c = cs.findById(id.longValue()).orElseThrow(NoSuchElementException::new);
        mav.addObject("community", c);
        mav.addObject("forumList", fs.findByCommunity(c));

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