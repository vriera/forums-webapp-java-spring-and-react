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
import org.springframework.http.ResponseEntity;
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

    @Autowired
    private UserService us;

    @Autowired
    private SearchService ss;

    @Autowired
    private ImageService is;
    @RequestMapping(path = "/")
    public ModelAndView landing() {
        final ModelAndView mav = new ModelAndView("landing");
        Optional<User> user = AuthenticationUtils.authorizeInView(mav, us);
        mav.addObject("community_list", cs.getVisibleList(user));



        return mav;
    }

    @RequestMapping(path = "/community/view/all", method=RequestMethod.GET)
    public ModelAndView allPost(@RequestParam(value = "query", required = false) String query,
                                @RequestParam(value = "filter" , required = false , defaultValue = "0") Number filter,
                                @RequestParam(value = "order", required = false , defaultValue = "0") Number order,
                                @ModelAttribute("paginationForm") PaginationForm paginationForm){
        final ModelAndView mav = new ModelAndView("community/all");
        Optional<User> auxuser = AuthenticationUtils.authorizeInView(mav, us);
        User u;
        try{ u = auxuser.get();}catch (Exception e ){ u = null;}
        List<Question> questionList = ss.search(query , filter , order , -1 , u , paginationForm.getLimit(), paginationForm.getLimit()*(paginationForm.getPage() - 1));
        List<Community> communityList = cs.getVisibleList(auxuser);
        List<Community> communitySearch = ss.searchCommunity(query);
        List<User> userSearch = ss.searchUser(query);
        mav.addObject("communitySearch" , communitySearch);
        mav.addObject("userSearch" , userSearch);
        mav.addObject("currentPage",paginationForm.getPage());
        int countQuestion = ss.countQuestionQuery(query , filter , order , -1 , u);
        mav.addObject("count",(Math.ceil((double)((int)countQuestion)/ paginationForm.getLimit())));
        mav.addObject("communityList", communityList);
        mav.addObject("questionList", questionList);
        mav.addObject("query", query);

        return mav;
    }




    @RequestMapping("/ask/community")
    public ModelAndView pickCommunity(){
        ModelAndView mav = new ModelAndView("ask/community");
        Optional<User> user = AuthenticationUtils.authorizeInView(mav, us);

        mav.addObject("communityList" , cs.getVisibleList(user));

        return mav;
    }



    @RequestMapping(path = "/community/view/{communityId}", method = RequestMethod.GET)
    public ModelAndView community(@PathVariable("communityId") Number communityId,
                                  @RequestParam(value = "query", required = false) String query,
                                  @RequestParam(value = "filter" , required = false , defaultValue = "0") Number filter,
                                  @RequestParam(value = "order", required = false , defaultValue = "0") Number order,
                                  @ModelAttribute("paginationForm") PaginationForm paginationForm){
        ModelAndView mav = new ModelAndView("community/view");
        Optional<User> maybeUser= AuthenticationUtils.authorizeInView(mav, us);

        Optional<Community> maybeCommunity = cs.findById(communityId);

        if(!maybeCommunity.isPresent()){
            return new ModelAndView("redirect:/404");
        }
        List<Question> questionList = ss.search(query , filter , order , communityId , maybeUser.orElse(null), paginationForm.getLimit(), paginationForm.getLimit()*(paginationForm.getPage() - 1));

        mav.addObject("currentPage",paginationForm.getPage());
        int questionCount = ss.countQuestionQuery(query , filter , order , communityId , maybeUser.orElse(null));
        mav.addObject("count",(Math.ceil((double)((int)questionCount)/ paginationForm.getLimit())));
        mav.addObject("query", query);
        mav.addObject("canAccess", cs.canAccess(maybeUser, maybeCommunity.get()));
        mav.addObject("community", maybeCommunity.get());
        mav.addObject("questionList", questionList);
        //Este justCreated solo esta en true cuando llego a esta vista despues de haberla creado. me permite mostrar una notificacion
        mav.addObject("communityList", cs.getVisibleList(maybeUser));
        mav.addObject("justCreated", false);
        return mav;
    }

    @RequestMapping("/community/select")
    public ModelAndView selectCommunity(){
        ModelAndView mav = new ModelAndView("community/select");
        Optional<User> user = AuthenticationUtils.authorizeInView(mav, us);

        mav.addObject("communityList", cs.getVisibleList(user));

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

    @RequestMapping(value = "/image/{id}" , method = RequestMethod.GET)
    public ResponseEntity<byte[]> getImage(@PathVariable("id") Number id ){
        Optional<Image> image = is.getImage(id);
        return ResponseEntity.ok().body(image.get().getImage());
    }
}