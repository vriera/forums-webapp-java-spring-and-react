package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.controller.utils.AuthenticationUtils;
import ar.edu.itba.paw.webapp.form.*;
import ar.edu.itba.paw.webapp.form.AnswersForm;
import ar.edu.itba.paw.webapp.form.CommunityForm;
import ar.edu.itba.paw.webapp.form.QuestionForm;
import ar.edu.itba.paw.webapp.form.UserForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.swing.*;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class GeneralController {
    @Autowired
    private CommunityService cs;

    //TODO: SALUS NO ME MATES SE QUE ESTO NO DEBE IR ACA PERO QUIERO AVANZAR
    @Autowired
    private UserService us;

    @Autowired
    private SearchService ss;

    @RequestMapping(path = "/")
    public ModelAndView landing() {
        final ModelAndView mav = new ModelAndView("landing");

        mav.addObject("community_list", cs.list());

        AuthenticationUtils.authorizeInView(mav, us);

        return mav;
    }

    @RequestMapping(path = "/community/view/all", method=RequestMethod.GET)
    public ModelAndView allPost(@RequestParam(value = "query", required = false) String query,  @ModelAttribute("paginationForm") PaginationForm paginationForm){
        final ModelAndView mav = new ModelAndView("community/all");
        AuthenticationUtils.authorizeInView(mav, us);

        List<Question> questionList = ss.search(query, paginationForm.getLimit(), paginationForm.getLimit()*(paginationForm.getPage() - 1));
        List<Community> communityList = cs.list();
        mav.addObject("currentPage",paginationForm.getPage());
        long countQuestion = ss.countQuestionQuery(query).get();
        mav.addObject("count",(Math.ceil((double)((int)countQuestion)/ paginationForm.getLimit())));
        mav.addObject("communityList", communityList);
        mav.addObject("questionList", questionList);
        mav.addObject("query", query);

        return mav;
    }


    @RequestMapping("/ask/community")
    public ModelAndView pickCommunity(){
        ModelAndView mav = new ModelAndView("ask/community");
        AuthenticationUtils.authorizeInView(mav, us);

        mav.addObject("communityList", cs.list());

        return mav;
    }



    @RequestMapping(path = "/community/view/{communityId}", method = RequestMethod.GET)
    public ModelAndView community(@PathVariable("communityId") Number communityId, @RequestParam(value = "query", required = false) String query,  @ModelAttribute("paginationForm") PaginationForm paginationForm){
        ModelAndView mav = new ModelAndView("community/view");
        AuthenticationUtils.authorizeInView(mav, us);

        Optional<User> maybeUser = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        Optional<Community> maybeCommunity = cs.findById(communityId);

        if(!maybeCommunity.isPresent()){
            return new ModelAndView("redirect:/404");
        }

        mav.addObject("currentPage",paginationForm.getPage());
        long questionCount = ss.countQuestionByCommunity(query,communityId).get();
        mav.addObject("count",(Math.ceil((double)((int)questionCount)/ paginationForm.getLimit())));
        mav.addObject("query", query);
        mav.addObject("canAccess", cs.canAccess(maybeUser, maybeCommunity.get()));
        mav.addObject("community", maybeCommunity.get());
        mav.addObject("questionList", ss.searchByCommunity(query, communityId, paginationForm.getLimit(), paginationForm.getLimit()*(paginationForm.getPage() - 1)));
        //Este justCreated solo esta en true cuando llego a esta vista despues de haberla creado. me permite mostrar una notificacion
        mav.addObject("communityList", cs.list());
        mav.addObject("justCreated", false);
        return mav;
    }

    @RequestMapping("/community/select")
    public ModelAndView selectCommunity(){
        ModelAndView mav = new ModelAndView("community/select");
        AuthenticationUtils.authorizeInView(mav, us);

        mav.addObject("communityList", cs.list());

        return mav;
    }


    @RequestMapping(path = "/community/create", method = RequestMethod.GET)
    public ModelAndView createCommunityGet(@ModelAttribute("communityForm") CommunityForm form){
        ModelAndView mav = new ModelAndView("community/create");
        AuthenticationUtils.authorizeInView(mav, us);
        return mav;
    }


    @RequestMapping(path="/community/create", method = RequestMethod.POST)
    public ModelAndView createCommunityPost(@ModelAttribute("communityForm") CommunityForm form){

        User owner = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(NoSuchElementException::new);
        Optional<Community> community = cs.create(form.getName(), form.getDescription(), owner);

        if(!community.isPresent())
            return new ModelAndView("redirect:/500"); //TODO: Si falló la creación, que intente de nuevo

        String redirect = String.format("redirect:/community/view/%d",community.get().getId());
        ModelAndView mav = new ModelAndView(redirect);
        AuthenticationUtils.authorizeInView(mav, us);
        mav.addObject("justCreated", true);
        return mav;
    }
}