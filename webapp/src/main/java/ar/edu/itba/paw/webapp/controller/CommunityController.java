package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.interfaces.services.CommunityService;
import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.auth.PawUserDetailsService;
import ar.edu.itba.paw.webapp.controller.dto.*;
import ar.edu.itba.paw.webapp.controller.dto.cards.CommunityCardDto;
import ar.edu.itba.paw.webapp.controller.utils.GenericResponses;
import ar.edu.itba.paw.webapp.controller.utils.PaginationHeaderUtils;
import ar.edu.itba.paw.webapp.form.CommunityForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Path("communities")
public class CommunityController {



    private final static int PAGE_SIZE = 10;

    @Autowired
    private CommunityService cs;

    @Autowired
    private UserService us;

    @Autowired
    private SearchService ss;

    @Autowired
    private PawUserDetailsService userDetailsService;

    @Context
    private UriInfo uriInfo;

    @Autowired
    private Commons commons;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    //Probably unused??
    //TODO paginar la list!!??

    /*
    @GET
    @Path("/community/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON , })
    //TODO
    public Response searchCommunity(@RequestParam(value = "query" , required = false , defaultValue = "") String query,
                                    @ModelAttribute("paginationForm") PaginationForm paginationForm){
    }*/



    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getCommunity(@PathParam("id") int id ) {
        User u = commons.currentUser();
        if( u == null){
            return GenericResponses.notAuthorized();
        }
        if(id<0){
            return GenericResponses.badRequest("Id cannot be negative");
        }

        Optional<Community> c = cs.findById(id);

//        if (!cs.canAccess(u, c.orElse(null))) {
//            return GenericResponses.cantAccess();
//        }

        CommunityDto cd = CommunityDto.communityToCommunityDto(c.orElse(null), uriInfo);

        return Response.ok(
                new GenericEntity<CommunityDto>(cd) {  //FIXME: The DTO should probably return the ID as well
                }
        ).build();
    }


    /*
    @RequestMapping("/community/search")
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

    //deprecated
    @GET
    @Path("/view/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response singleCommunity(@PathParam("id") int id,
                                    @DefaultValue("") @QueryParam("query") String query,
                                    @DefaultValue("0") @QueryParam("filter") int filter,
                                    @DefaultValue("0") @QueryParam("order") int order,
                                    @DefaultValue("1") @QueryParam("page") int page,
                                    @DefaultValue("10") @QueryParam("size") int size) {

        int offset = (page - 1) * size;
        int limit = size;

        User u = commons.currentUser();
        Optional<Community> c = cs.findById(u.getId());

        if (!cs.canAccess(u, c.orElse(null))) {
            return GenericResponses.cantAccess();

        }

        List<Question> questionList = ss.search(query, SearchFilter.values()[filter], SearchOrder.values()[order], id, u, limit, offset);

        int questionCount = ss.countQuestionQuery(query, SearchFilter.values()[filter], SearchOrder.values()[order], id, u);
        int pages = (int) Math.ceil((double) questionCount / size);

        CommunitySearchDto csDto = CommunitySearchDto.QuestionListToCommunitySearchDto(questionList, uriInfo, id, query, filter, order, page, size, pages);
        return Response.ok(

                new GenericEntity<CommunitySearchDto>(csDto) {
                }

        ).build();
    }*/


    @POST
    @Path("/")
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response create(@Valid final CommunityForm communityForm) {
        final User u = commons.currentUser();

        if( u == null ){
            //TODO mejores errores
            return GenericResponses.notAuthorized();
        }
        //Name
        //Description
        //TODO: the validations

        final String title = communityForm.getName();
        final String description = communityForm.getDescription();
        Optional<Community> c = cs.create(title, description, u);

        if (!c.isPresent()) {
            return GenericResponses.serverError();
        }

        return Response.ok(
                new GenericEntity<CommunityDto>(CommunityDto.communityToCommunityDto(c.get(), uriInfo)) {}
        ).build();
    }


    //veremos
    @PUT
    @Path("/{communityId}/user/{userId}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response access(@Valid AccessDto accessDto , @PathParam("userId") final long userId, @PathParam("communityId") final long communityId){
        String accessTypeParam = accessDto.getAccessType();
        final User currentUser = commons.currentUser();
        if(currentUser == null){
            return GenericResponses.notAuthorized();
        }
        final long authorizerId = currentUser.getId();
        LOGGER.info("User {} tried to access community {} with target user {} and desired access type {}" , currentUser.getId(), communityId, userId, accessTypeParam);

        boolean success = false;
        LOGGER.debug("canInteract = {}, canAuthorize = {}", canInteract(userId, authorizerId), canAuthorize(communityId, authorizerId));
        String code = "unknown.error";
        AccessType desiredAccessType;
        try{
            desiredAccessType = AccessType.valueOf(accessTypeParam);
        }
        catch(IllegalArgumentException e){
            if(accessTypeParam.equals("NONE")){
                Optional<AccessType> currentAccess = cs.getAccess(userId, communityId);
                LOGGER.debug("Entrando al switch con access {}", "NONE");
                // Both these operations result in a reset of interactions between user and community
                if(currentAccess.isPresent() && currentAccess.get() == AccessType.BLOCKED_COMMUNITY){
                    if(!canInteract(userId, authorizerId)){
                        return GenericResponses.notAuthorized();
                    }
                    success = cs.unblockCommunity(userId, communityId);
                }
                else if(currentAccess.isPresent() && currentAccess.get() == AccessType.BANNED){
                    if(!canAuthorize(communityId, authorizerId)){
                        return GenericResponses.notAuthorized();
                    }
                    success = cs.liftBan(userId, communityId, authorizerId);
                }
            }
            return success? GenericResponses.success() : GenericResponses.badRequest("unknown.access.type");
        }

        LOGGER.debug("Entrando al switch con access {}", desiredAccessType);

        switch (desiredAccessType) {
            case ADMITTED: {
                if (canAuthorize(communityId, authorizerId)) {
                    success = cs.admitAccess(userId, communityId, authorizerId);
                    code = "cannot.authorize.access";
                } else if (canInteract(userId, authorizerId)) {
                    code = "cannot.accept.invite";
                    success = cs.acceptInvite(userId, authorizerId);
                } else {
                    return GenericResponses.notAuthorized();
                }
                break;
            }
            case KICKED: {
                if (!canAuthorize(communityId, authorizerId)) {
                    return GenericResponses.notAuthorized();
                }
                success = cs.kick(userId, communityId, authorizerId);
                code = "cannot.kick.user";
                break;
            }
            case BANNED: {
                if (!canAuthorize(communityId, authorizerId)) {
                    return GenericResponses.notAuthorized();
                }
                success = cs.ban(userId, communityId, authorizerId);
                code = "cannot.ban.user";
                break;
            }
            case REQUEST_REJECTED: {
                if (!canAuthorize(communityId, authorizerId)) {
                    return GenericResponses.notAuthorized();
                }
                success = cs.rejectAccess(userId, communityId, authorizerId);
                code = "cannot.reject.request";
                break;
            }
            case INVITED: {
                if (!canAuthorize(communityId, authorizerId)) {
                    return GenericResponses.notAuthorized();
                }
                code = "cannot.invite.user";
                success = cs.invite(userId, communityId, authorizerId);
                break;
            }
            case REQUESTED: {
                if (!canInteract(userId, authorizerId)) {
                    return GenericResponses.notAuthorized();
                }
                success = cs.requestAccess(userId, communityId);
                code = "cannot.request.access";
                break;
            }
            case INVITE_REJECTED: {
                if (!canInteract(userId, authorizerId)) {
                    return GenericResponses.notAuthorized();
                }
                success = cs.refuseInvite(userId, communityId);
                code = "cannot.reject.invite";
                break;
            }
            case LEFT: {
                if (!canInteract(userId, authorizerId)) {
                    return GenericResponses.notAuthorized();
                }
                success = cs.leaveCommunity(userId, communityId);
                code = "cannot.leave.community";
                break;
            }
            case BLOCKED_COMMUNITY: {
                if (!canInteract(userId, authorizerId)) {
                    return GenericResponses.notAuthorized();
                }
                success = cs.blockCommunity(userId, communityId);
                code = "cannot.block.community";
                break;
            }
        }

        return success? GenericResponses.success() : GenericResponses.badRequest(code);

    }



    private boolean canAuthorize(long communityId, long authorizerId){
        Optional<Community> maybeCommunity = cs.findById(communityId);

        // Si el autorizador no es el moderador, no tiene acceso a la acción
        return maybeCommunity.isPresent() && authorizerId == maybeCommunity.get().getModerator().getId();
    }
    private boolean canInteract(long userId, long authorizerId){
        return  authorizerId == userId;
    }


}



