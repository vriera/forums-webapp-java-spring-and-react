package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.Answer;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.form.AnswersForm;
import ar.edu.itba.paw.webapp.form.QuestionForm;
import ar.edu.itba.paw.webapp.form.UserForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

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
    private AnswersService as;

    @Autowired
    private MailingService ms;

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

    @RequestMapping("/question/{id}")
    public ModelAndView answer(@ModelAttribute("AnswersForm") AnswersForm form, @PathVariable("id") long id){
        ModelAndView mav = new ModelAndView("ask/answer");
        List<Answer> answersList = as.findByQuestionId(id);
        Optional<Question> question = qs.findById(id);
        mav.addObject("answerList", answersList);
        mav.addObject("question",question.get()); //falta verificar que exista la pregunta
        return mav;
    }

    @RequestMapping(path = "/question/{id}" , method = RequestMethod.POST)
    public ModelAndView createAnswerPost( @ModelAttribute("AnswersForm") AnswersForm form,@PathVariable("id") long id ){
        Optional<User> u = us.findByEmail(form.getEmail());
        Optional<Question> question = qs.findById(id);
        if(!u.isPresent()){
            u = us.create(form.getName(), form.getEmail());
        }
        if(u.isPresent() && question.isPresent()){
            Optional<Answer> answer = as.create(form.getBody(),u.get(),id);
            if(answer.isPresent()){
                ms.sendMail(question.get().getOwner().getEmail(),"Verificar Respuesta",answer.get().getBody());
            }
        }
        String redirect = String.format("redirect:/question/%d",id);
        return new ModelAndView(redirect);
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
}