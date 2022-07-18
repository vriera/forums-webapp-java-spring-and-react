package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CommunityService;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.auth.PawUserDetailsService;
import ar.edu.itba.paw.webapp.controller.utils.AuthenticationUtils;
import ar.edu.itba.paw.webapp.form.CommunityForm;
import ar.edu.itba.paw.webapp.form.PaginationForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Controller
public class GeneralController {
    @Autowired
    private CommunityService cs;

    @Autowired
    private UserService us;

    @Autowired
    private SearchService ss;

    @Autowired
    private PawUserDetailsService userDetailsService;


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

    @RequestMapping(path = "/top")
    public ModelAndView top() {
        final ModelAndView mav = new ModelAndView("top");
        Optional<User> user = AuthenticationUtils.authorizeInView(mav, us);
        Number id;
        if(user.isPresent()){
            id = user.get().getId();
        }else
        {
            id = -1;
        }
        mav.addObject("answerList", ss.getTopAnswers(id));

        AuthenticationUtils.authorizeInView(mav, us);

        return mav;
    }


    //DONE
    @RequestMapping(path = "/user/{userId}")
    public ModelAndView otheruserProfile(@PathVariable("userId") Number userId) {
        final ModelAndView mav = new ModelAndView("user/view");
        AuthenticationUtils.authorizeInView(mav, us);
        Optional<User> maybeUser = us.findById(userId.longValue());
        if ( maybeUser.isPresent() ) {
            User user = maybeUser.get();
            mav.addObject("otherUser", user);
            mav.addObject("karma" , us.getKarma(userId).orElse(new Karma(null , -1L)).getKarma());
        }else {
            mav.addObject("text_variable" , "No user");
            return new ModelAndView("redirect:/404");
        }
        return mav;
    }

    //DONE
    @RequestMapping(path= "/user/moderatedCommunities/{userId}")
    public ModelAndView otherUserProfileCommunities(@PathVariable("userId") Number userId, @RequestParam(name = "page", required = false, defaultValue = "0") Number page){
        final ModelAndView mav = new ModelAndView("user/moderatedCommunities");
        AuthenticationUtils.authorizeInView(mav, us);
        Optional<User> maybeUser = us.findById(userId.longValue());
        if ( maybeUser.isPresent() ) {
            User user = maybeUser.get();
            mav.addObject("otherUser", user);
            mav.addObject("communities", us.getModeratedCommunities(user.getId(), page));
            mav.addObject("page", page);
            Long totalPages = us.getModeratedCommunitiesPages(user.getId());
            mav.addObject("totalPages", totalPages);
        }
        return mav;
    }



    //DONE
    @RequestMapping(path = "/community/view/all", method=RequestMethod.GET)
    public ModelAndView allPost(@RequestParam(value = "query", required = false, defaultValue= "") String query,
                                @RequestParam(value = "filter" , required = false , defaultValue = "0") Number filter,
                                @RequestParam(value = "order", required = false , defaultValue = "0") Number order,
                                @ModelAttribute("paginationForm") PaginationForm paginationForm){
        final ModelAndView mav = new ModelAndView("community/all");
        Optional<User> maybeUser = AuthenticationUtils.authorizeInView(mav, us);
        User u = maybeUser.orElse(null);
        List<Question> questionList = ss.search(query , SearchFilter.values()[filter.intValue()] , SearchOrder.values()[order.intValue()] , -1 , u , paginationForm.getLimit(), paginationForm.getLimit()*(paginationForm.getPage() - 1));
        List<Community> communityList = cs.list(u);

        Number currentPage = (Number) paginationForm.getPage();
        int countQuestion = ss.countQuestionQuery(query , SearchFilter.values()[filter.intValue()] , SearchOrder.values()[order.intValue()] , -1 , u);
        int totalPages = (int) (Math.ceil((double)((int)countQuestion)/ paginationForm.getLimit()));
        adjustPage(currentPage, totalPages);
        mav.addObject("currentPage",currentPage.intValue());
        mav.addObject("count",totalPages);
        mav.addObject("communityList", communityList);
        mav.addObject("questionList", questionList);
        mav.addObject("query", query);
        return mav;
    }



    public ModelAndView searchCommunity(@RequestParam(value = "query" , required = false , defaultValue = "") String query,
                                        @ModelAttribute("paginationForm") PaginationForm paginationForm){
        ModelAndView mav = new ModelAndView("search/community");
        Optional<User> maybeUser = AuthenticationUtils.authorizeInView(mav , us );
        //mav.addObject("communityList" , cs.list(maybeUser.orElse(null)));
        int countCommunity = ss.searchCommunityCount(query);
        mav.addObject("communitySearchList" , ss.searchCommunity(query ,paginationForm.getLimit(), paginationForm.getLimit()*(paginationForm.getPage() - 1)));
        mav.addObject("count",(Math.ceil((double)((int)countCommunity)/ paginationForm.getLimit())));
        mav.addObject("currentPage",paginationForm.getPage());
        mav.addObject("communityList" , cs.list(maybeUser.orElse(null)));
        mav.addObject("query", query);
        return mav;
    }

    //TODO
    @RequestMapping("/user/search")
    public ModelAndView searchUser(@RequestParam(value = "query" , required = false , defaultValue = "") String query,
     @ModelAttribute("paginationForm") PaginationForm paginationForm) {
        ModelAndView mav = new ModelAndView("search/user");
        Optional<User> maybeUser = AuthenticationUtils.authorizeInView(mav, us);
        int countUser = ss.searchUserCount(query);
        mav.addObject("count", (Math.ceil((double) ((int) countUser) / paginationForm.getLimit())));
        mav.addObject("currentPage", paginationForm.getPage());
        mav.addObject("communityList", cs.list(maybeUser.orElse(null)));
        mav.addObject("userList", ss.searchUser(query, paginationForm.getLimit(), paginationForm.getLimit() * (paginationForm.getPage() - 1)));
        mav.addObject("query", query);
        return mav;
    }


    //TODO
    @RequestMapping("/ask/community")
    public ModelAndView pickCommunity(){
        ModelAndView mav = new ModelAndView("ask/community");
        Optional<User> maybeUser = AuthenticationUtils.authorizeInView(mav, us);

        mav.addObject("communityList", cs.list(maybeUser.orElse(null)));

        return mav;
    }


    //DONE
    @RequestMapping(path = "/community/view/{communityId}", method = RequestMethod.GET)
    public ModelAndView community(@PathVariable("communityId") Number communityId,
                                  @RequestParam(value = "query", required = false , defaultValue = "") String query,
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
        mav.addObject("userCount" , cs.getUserCount(communityId).orElse(0).longValue() + 1)  ;
        List<Question> questionList = ss.search(query , SearchFilter.values()[filter.intValue()] , SearchOrder.values()[order.intValue()] , communityId , maybeUser.orElse(null), paginationForm.getLimit(), paginationForm.getLimit()*(paginationForm.getPage() - 1));

        int questionCount = ss.countQuestionQuery(query , SearchFilter.values()[filter.intValue()] , SearchOrder.values()[order.intValue()] , communityId , maybeUser.orElse(null));

        Number currentPage = paginationForm.getPage();
        int totalPages = (int) (Math.ceil((double)(questionCount)/ paginationForm.getLimit()));
        adjustPage(currentPage,totalPages);
        mav.addObject("currentPage",currentPage.intValue());
        mav.addObject("count", totalPages);

        mav.addObject("query", query);
        mav.addObject("canAccess", cs.canAccess(maybeUser.orElse(null), maybeCommunity.get()));
        mav.addObject("community", maybeCommunity.get());
        mav.addObject("questionList", questionList);
        //Este justCreated solo está en true cuando llego a esta vista después de haberla creado. me permite mostrar una notificacion
        mav.addObject("communityList", cs.list(maybeUser.orElse(null)));
        mav.addObject("justCreated", false);
        return mav;
    }

    //DONE
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

    //TODO
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
        AuthenticationUtils.authenticate(owner.getEmail(), userDetailsService); //Actualizamos la sesión para reflejar que ahora es un moderador
        return mav;
    }


    //TODO?
    @RequestMapping(value = "/image/{id}" , method = RequestMethod.GET)
    public ResponseEntity<byte[]> getImage(@PathVariable("id") Number id ){
        Optional<Image> image = is.getImage(id);
        return ResponseEntity.ok().body(image.get().getImage());
    }


    //Lleva la página al extremo de [0,pageCap] más cercano si se salió del intervalo
    private void adjustPage(Number pageNumber, long pageCap){
        if(pageNumber.longValue() < 0)
            pageNumber = 1;
        else if(pageNumber.longValue() > pageCap)
            pageNumber = pageCap;
    }



}