package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Forum;
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

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class GeneralController {
    @Autowired
    private UserService us;

    @Autowired
    private QuestionService qs;

    @Autowired
    private CommunityService cs;

    @Autowired
    private ForumService fs;

    @Autowired
    private SearchService ss;

    @RequestMapping(path = "/")
    public ModelAndView landing() {
        final ModelAndView mav = new ModelAndView("landing");

        mav.addObject("community_list", cs.list());

        return mav;
    }

    @RequestMapping(path = "/all", method=RequestMethod.GET)
    public ModelAndView allPost(@RequestParam(value = "query", required = false) String query){
        final ModelAndView mav = new ModelAndView("all");

        List<Question> questionList = ss.search(query);
        List<Community> communityList = cs.list();

        mav.addObject("communityList", communityList);
        mav.addObject("questionList", questionList);
        mav.addObject("query", query);

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
        mav.addObject("forumList", fs.findByCommunity(id));
        return mav;
    }

    @RequestMapping(path = "/ask/question" , method = RequestMethod.POST)
    public ModelAndView createQuestionPost( @ModelAttribute("questionForm") QuestionForm form){
        //ModelAndView mav = new ModelAndView("ask/question");
        Integer key = qs.addTemporaryQuestion(form.getTitle() , form.getBody() , form.getCommunity(), form.getForum());
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
        Question question = qs.removeTemporaryQuestion(userForm.getKey().intValue());
        question.setOwner(new User(0L, userForm.getName() , userForm.getEmail()));
        Optional<Question> q = qs.create(question);
        return new ModelAndView("redirect:/ask/finish?success="+q.isPresent());
    }

    @RequestMapping("/ask/finish")
    public ModelAndView uploadQuestion(@RequestParam("success") Boolean success){
        ModelAndView mav = new ModelAndView("ask/finish");

        mav.addObject("success", success);

        return  mav;
    }

    @RequestMapping(path = "/community/view", method = RequestMethod.GET)
    public ModelAndView community(@RequestParam("communityId") Number communityId, @RequestParam(value = "query", required = false) String query){
        ModelAndView mav = new ModelAndView("community/view");

        Optional<Community> maybeCommunity = cs.findById(communityId);

        if(!maybeCommunity.isPresent()){
            return new ModelAndView("redirect:/404"); //TODO: 404
        }

        mav.addObject("community", maybeCommunity.get());
        mav.addObject("questionList", ss.searchByCommunity(query, communityId));
        mav.addObject("communityList", cs.list().stream().filter(community -> community.getId() != communityId.longValue()).collect(Collectors.toList()));

        return mav;
    }

    @RequestMapping("/community/select")
    public ModelAndView selectCommunity(){
        ModelAndView mav = new ModelAndView("community/select");

        mav.addObject("communityList", cs.list());

        return mav;
    }
}