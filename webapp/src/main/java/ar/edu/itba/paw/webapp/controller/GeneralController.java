package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.controller.utils.AuthenticationUtils;
import ar.edu.itba.paw.webapp.form.*;
import ar.edu.itba.paw.webapp.form.AnswersForm;
import ar.edu.itba.paw.webapp.form.CommunityForm;
import ar.edu.itba.paw.webapp.form.QuestionForm;
import ar.edu.itba.paw.webapp.form.UserForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(GeneralController.class);

    @Autowired
    private ImageService is;
    @RequestMapping(path = "/")
    public ModelAndView landing() {
        final ModelAndView mav = new ModelAndView("landing");
        Optional<User> user = AuthenticationUtils.authorizeInView(mav, us);
        mav.addObject("community_list", cs.list(user.orElse(null)));

        AuthenticationUtils.authorizeInView(mav, us);

        return mav;
    }
    @RequestMapping(path = "/karma")
    public ModelAndView karma() {
        final ModelAndView mav = new ModelAndView("blank/blank");
        Optional<User> user = AuthenticationUtils.authorizeInView(mav, us);
        if ( user.isPresent() ) {
            mav.addObject("test_variable" , us.getKarma(user.get().getId()).orElse(new Karma(null , -1L)).getKarma());
        }else
        {
            mav.addObject("text_variable" , "No user");
        }
        return mav;
    }

    @RequestMapping(path = "/community/view/all", method=RequestMethod.GET)
    public ModelAndView allPost(@RequestParam(value = "query", required = false) String query,
                                @RequestParam(value = "filter" , required = false , defaultValue = "0") Number filter,
                                @RequestParam(value = "order", required = false , defaultValue = "0") Number order,
                                @ModelAttribute("paginationForm") PaginationForm paginationForm){
        final ModelAndView mav = new ModelAndView("community/all");
        Optional<User> maybeUser = AuthenticationUtils.authorizeInView(mav, us);
        User u = maybeUser.orElse(null);

        List<Question> questionList = ss.search(query , filter , order , -1 , u , paginationForm.getLimit(), paginationForm.getLimit()*(paginationForm.getPage() - 1));
        List<Community> communityList = cs.list(u);
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
        Optional<User> maybeUser = AuthenticationUtils.authorizeInView(mav, us);

        mav.addObject("communityList", cs.list(maybeUser.orElse(null)));

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
            mav = new ModelAndView("redirect:/404");
            AuthenticationUtils.authorizeInView(mav, us);
            return mav;
        }

        List<Question> questionList = ss.search(query , filter , order , communityId , maybeUser.orElse(null), paginationForm.getLimit(), paginationForm.getLimit()*(paginationForm.getPage() - 1));

        mav.addObject("currentPage",paginationForm.getPage());
        int questionCount = ss.countQuestionQuery(query , filter , order , communityId , maybeUser.orElse(null));
        mav.addObject("count",(Math.ceil((double)(questionCount)/ paginationForm.getLimit())));
        mav.addObject("query", query);
        mav.addObject("canAccess", cs.canAccess(maybeUser.orElse(null), maybeCommunity.get()));
        mav.addObject("community", maybeCommunity.get());
        mav.addObject("questionList", questionList);
        //Este justCreated solo está en true cuando llego a esta vista después de haberla creado. me permite mostrar una notificacion
        mav.addObject("communityList", cs.list(maybeUser.orElse(null)));
        mav.addObject("justCreated", false);
        return mav;
    }

    @RequestMapping("/community/select")
    public ModelAndView selectCommunity(){
        ModelAndView mav = new ModelAndView("community/select");
        Optional<User> maybeUser = AuthenticationUtils.authorizeInView(mav, us);

        mav.addObject("communityList", cs.list(maybeUser.orElse(null)));

        return mav;
    }


    @RequestMapping(path = "/community/create", method = RequestMethod.GET)
    public ModelAndView createCommunityGet(@ModelAttribute("communityForm") CommunityForm form, boolean nameTaken){
        ModelAndView mav = new ModelAndView("community/create");
        AuthenticationUtils.authorizeInView(mav, us);
        mav.addObject("nameTaken", nameTaken);
        return mav;
    }


    @RequestMapping(path="/community/create", method = RequestMethod.POST)
    public ModelAndView createCommunityPost(@ModelAttribute("communityForm") @Valid CommunityForm form, BindingResult errors){

        if(errors.hasErrors())
            return createCommunityGet(form, false);

        User owner = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(NoSuchElementException::new);
        Optional<Community> community = cs.create(form.getName(), form.getDescription(), owner);

        if(!community.isPresent())
            return createCommunityGet(form, true); //La única otra fuente de error son campos vacíos, que se atajan en el form

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