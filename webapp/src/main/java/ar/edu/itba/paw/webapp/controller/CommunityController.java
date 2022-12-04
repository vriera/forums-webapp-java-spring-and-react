package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.interfaces.services.CommunityService;
import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.auth.PawUserDetailsService;
import ar.edu.itba.paw.webapp.controller.dto.CommunityDto;
import ar.edu.itba.paw.webapp.controller.dto.CommunityListDto;
import ar.edu.itba.paw.webapp.controller.dto.QuestionSearchDto;
import ar.edu.itba.paw.webapp.controller.dto.UserListDto;
import ar.edu.itba.paw.webapp.controller.utils.GenericResponses;
import ar.edu.itba.paw.webapp.form.CommunityForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Optional;

@Component
@Path("community")
public class CommunityController {

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
    @Path("")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response list(@DefaultValue("0") @QueryParam("page") int page, @DefaultValue("5") @QueryParam("size") int size , @DefaultValue("") @QueryParam("query") String query) {
        int offset = (page) * size;
        if(size < 1 )
            size = 1;
        int limit = size;
        User u = commons.currentUser();
        List<Community> cl = ss.searchCommunity(query , limit, offset);
        long total = (long) Math.ceil(ss.searchCommunityCount(query) / (double)size);
        CommunityListDto cld = CommunityListDto.communityListToCommunityListDto(cl, uriInfo, query, page, size,total);

        return Response.ok(
                new GenericEntity<CommunityListDto>(cld) {
                }
        ).build();
    }


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

        if (!cs.canAccess(u, c.orElse(null))) {
            return GenericResponses.cantAccess();
        }

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
    @Path("/create/{moderatorId}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response create(@PathParam("moderatorId") final int id , @Valid final CommunityForm communityForm) {
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

    //Banned
    @GET
    @Path("/{communityId}/user/{userId}/banned")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response bannedUsers(@PathParam("communityId") final int communityId , @DefaultValue("1")  @QueryParam("page") final int page ){

        final User u = commons.currentUser();

        if( u == null){
            //TODO mejores errores
            return GenericResponses.notAuthorized();
        }

        if(!us.isModerator(u.getId(), communityId)){
            return GenericResponses.notAuthorized();
        }


        long bannedPages = cs.getMemberByAccessTypePages(communityId, AccessType.BANNED);
        long pageSize = 10;

        List<User> ul = cs.getMembersByAccessType(communityId,AccessType.BANNED , page - 1);

        UserListDto userListDto = UserListDto.userListToUserListDto(ul , uriInfo, (long) page, pageSize,bannedPages);

        return Response.ok(
                new GenericEntity<UserListDto>(userListDto){}
        ).build();

    }

    //Admitted
    @GET
    @Path("/{communityId}/user/{userId}/admitted")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response admittedUsers(@PathParam("communityId") final int communityId , @DefaultValue("1")  @QueryParam("page") final int page ){

        final User u = commons.currentUser();

        if(u == null ){
            //TODO mejores errores
            return GenericResponses.notAuthorized();
        }

        if(!us.isModerator(u.getId() , communityId)){
            return GenericResponses.notAuthorized();
        }


        long pages = cs.getMemberByAccessTypePages(communityId, AccessType.ADMITTED);
        long pageSize = 10;


        List<User> ul = cs.getMembersByAccessType(communityId,AccessType.ADMITTED , page - 1);

        UserListDto userListDto = UserListDto.userListToUserListDto(ul , uriInfo, (long) page, pageSize,pages);

        return Response.ok(
                new GenericEntity<UserListDto>(userListDto){}
        ).build();

    }

    //invited
    @GET
    @Path("/{communityId}/user/{moderatorId}/invited")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response invitedUsers(@PathParam("communityId") final int communityId, @DefaultValue("1")  @QueryParam("page") final int page ){

        final User u = commons.currentUser();

        if(u == null ){
            //TODO mejores errores
            return GenericResponses.notAuthorized();
        }

        if(!us.isModerator(u.getId() , communityId)){
            return GenericResponses.notAuthorized();
        }

        long pages = cs.getMemberByAccessTypePages(communityId, AccessType.INVITED);
        long pageSize = 10;

        List<User> ul = cs.getMembersByAccessType(communityId,AccessType.INVITED , page - 1);

        UserListDto userListDto = UserListDto.userListToUserListDto(ul , uriInfo, (long) page, pageSize,pages);

        return Response.ok(
                new GenericEntity<UserListDto>(userListDto){}
        ).build();

    }

    //invited
    @GET
    @Path("/{communityId}/user/{userId}/requested")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response requestedUsers(@PathParam("communityId") final int communityId , @DefaultValue("1")  @QueryParam("page") final int page ){

        final User u = commons.currentUser();

        if(u == null){
            //TODO mejores errores
            return GenericResponses.notAuthorized();
        }

        if(!us.isModerator(u.getId(), communityId)){
            return GenericResponses.notAuthorized();
        }

        long pages = cs.getMemberByAccessTypePages(communityId, AccessType.REQUESTED);
        long pageSize = 10;

        List<User> ul = cs.getMembersByAccessType(communityId,AccessType.REQUESTED , page - 1);

        UserListDto userListDto = UserListDto.userListToUserListDto(ul , uriInfo, (long) page, pageSize,pages);

        return Response.ok(
                new GenericEntity<UserListDto>(userListDto){}
        ).build();

    }
    //invite-rejected
    @GET
    @Path("/{communityId}/user/{userId}/invite-rejected")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response inviteRejectedUsers(@PathParam("communityId") final int communityId , @DefaultValue("1")  @QueryParam("page") final int page ){

        final User u = commons.currentUser();

        if( u == null){
            return GenericResponses.notAuthorized();
        }

        if(!us.isModerator(u.getId() , communityId)){
            return GenericResponses.notAuthorized();
        }

        long pageSize = 10;
        long pages = cs.getMemberByAccessTypePages(communityId, AccessType.INVITE_REJECTED);

        List<User> ul = cs.getMembersByAccessType(communityId,AccessType.INVITE_REJECTED , page - 1);

        UserListDto userListDto = UserListDto.userListToUserListDto(ul , uriInfo, (long) page, pageSize,pages);

        return Response.ok(
                new GenericEntity<UserListDto>(userListDto){}
        ).build();

    }





}



