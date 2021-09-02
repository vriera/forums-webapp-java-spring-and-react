package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.form.QuestionForm;
import ar.edu.itba.paw.webapp.form.UserForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
    @Autowired
    private TemporalQuestionService tqs;
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

    @RequestMapping(path = "/ask/question" , method = RequestMethod.GET)
    public ModelAndView createQuestionGet(@RequestParam("communityId") Number id , @ModelAttribute("questionForm") QuestionForm form){
        ModelAndView mav = new ModelAndView("ask/question");

        Community c = cs.findById(id.longValue()).orElseThrow(NoSuchElementException::new);
        mav.addObject("community", c);
        mav.addObject("forumList", fs.findByCommunity(c));
        return mav;
    }

    @RequestMapping(path = "/ask/question" , method = RequestMethod.POST)
    public ModelAndView createQuestionPost( @ModelAttribute("questionForm") QuestionForm form){
        //ModelAndView mav = new ModelAndView("ask/question");
        Integer key = tqs.addTemporalQuestion(form.getTitle() , form.getBody() , form.getCommunity(), form.getForum());
        return new ModelAndView("redirect:/ask/contact?key=" + key);
    }

    @RequestMapping(path = "/ask/contact" , method = RequestMethod.GET)
    public ModelAndView setContact(@ModelAttribute("userForm") UserForm userForm , @RequestParam("key") Number key ){
        ModelAndView mav = new ModelAndView("ask/contact");
        mav.addObject("key" , key);
        return mav;
    }

    @RequestMapping(path = "/ask/contact" , method = RequestMethod.POST)
    public ModelAndView setContact( @ModelAttribute("userForm") UserForm userForm){
        Question question = tqs.removeTemporalQuestion(userForm.getKey().intValue());
        question.setOwner(new User(0L, userForm.getName() , userForm.getEmail()));
        qs.create(question);
        return new ModelAndView("redirect:/");
    }

    @RequestMapping("/ask/finish")
    public ModelAndView uploadQuestion(){
        ModelAndView mav = new ModelAndView("ask/finish");

        Boolean success = true;
        mav.addObject("success", success);

        return  mav;
    }
}