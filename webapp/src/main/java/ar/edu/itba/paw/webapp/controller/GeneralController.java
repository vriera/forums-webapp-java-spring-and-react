package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.Answer;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.form.AnswersForm;
import ar.edu.itba.paw.webapp.form.CommunityForm;
import ar.edu.itba.paw.webapp.form.QuestionForm;
import ar.edu.itba.paw.webapp.form.UserForm;
import jdk.nashorn.internal.runtime.regexp.joni.constants.OPCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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
    private AnswersService as;

    @Autowired
    private SearchService ss;
    @Autowired
    private MailingService ms;
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


    @RequestMapping("/question/{id}")
    public ModelAndView answer(@ModelAttribute("AnswersForm") AnswersForm form, @PathVariable("id") long id){
        ModelAndView mav = new ModelAndView("question/answer");
        List<Answer> answersList = as.findByQuestionId(id);
        Optional<Question> question = qs.findById(id);
        mav.addObject("answerList", answersList);
        mav.addObject("question",question.get()); //falta verificar que exista la pregunta

        mav.addObject("communityList", cs.list().stream().filter(community -> community.getId() != question.get().getCommunity().getId().longValue()).collect(Collectors.toList()));

        return mav;
    }

    @RequestMapping(path = "/question/{id}" , method = RequestMethod.POST)
    public ModelAndView createAnswerPost( @ModelAttribute("AnswersForm") AnswersForm form,@PathVariable("id") long id ){
        Optional<Question> question = qs.findById(id);
        Optional<Answer> answer = as.create(form.getBody(), form.getName(), form.getEmail(), id);
        if(answer.isPresent()){
            ms.sendAnswerVeify(question.get().getOwner().getEmail(),question.get(),answer.get());
        }
        String redirect = String.format("redirect:/question/%d",id);
        return new ModelAndView(redirect);
    }


    @RequestMapping("/answer/{id}/verify/")
    public ModelAndView verifyAnswer(@PathVariable("id") long id){

        Optional<Answer> answer = as.verify(id);
        String redirect = String.format("redirect:/question/%d",answer.get().getId_question());
        return new ModelAndView(redirect);
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
        Optional<Question> question = qs.removeTemporaryQuestion(userForm.getKey().intValue(), userForm.getName() , userForm.getEmail());
       /* question.setOwner(new User(userForm.getName() , userForm.getEmail()));
        Optional<Question> q = qs.create(question);
        */

        return new ModelAndView("redirect:/ask/finish?success="+question.isPresent());
    }

    @RequestMapping("/ask/finish")
    public ModelAndView uploadQuestion(@RequestParam("success") Boolean success){
        ModelAndView mav = new ModelAndView("ask/finish");

        mav.addObject("success", success);

        return  mav;
    }

    @RequestMapping(path = "/community/view/{communityId}", method = RequestMethod.GET)
    public ModelAndView community(@PathVariable("communityId") Number communityId, @RequestParam(value = "query", required = false) String query){
        ModelAndView mav = new ModelAndView("community/view");

        Optional<Community> maybeCommunity = cs.findById(communityId);

        if(!maybeCommunity.isPresent()){
            return new ModelAndView("redirect:/404");
        }

        mav.addObject("query", query);
        mav.addObject("community", maybeCommunity.get());
        mav.addObject("questionList", ss.searchByCommunity(query, communityId));
        mav.addObject("communityList", cs.list().stream().filter(community -> community.getId() != communityId.longValue()).collect(Collectors.toList()));
        //Este justCreated solo esta en true cuando llego a esta vista despues de haberla creado. me permite mostrar una notificacion
        mav.addObject("justCreated", false);
        return mav;
    }

    @RequestMapping("/community/select")
    public ModelAndView selectCommunity(){
        ModelAndView mav = new ModelAndView("community/select");

        mav.addObject("communityList", cs.list());

        return mav;
    }


    @RequestMapping(path = "/community/create", method = RequestMethod.GET)
    public ModelAndView createCommunityGet(@ModelAttribute("communityForm") CommunityForm form){
        ModelAndView mav = new ModelAndView("community/create");
        return mav;
    }


    @RequestMapping(path="/community/create", method = RequestMethod.POST)
    public ModelAndView createCommunityPost(@ModelAttribute("communityForm") CommunityForm form){

        //TODO: aca meti un placeholder de usuario, habria que cambiarlo por el usuario activo.
        User user = us.list().get(0);
        Optional<Community> community = cs.create(form.getName(), form.getDescription(), user );
        String redirect = String.format("redirect:/community/view/%d",community.get().getId());
        ModelAndView mav = new ModelAndView(redirect);
        mav.addObject("justCreated", true);
        return mav;
    }
}