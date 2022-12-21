package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CommunityService;
import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.AccessType;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.User;

import ar.edu.itba.paw.webapp.controller.dto.cards.CommunityCardDto;
import ar.edu.itba.paw.webapp.controller.utils.GenericResponses;
import ar.edu.itba.paw.webapp.controller.utils.PaginationHeaderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
@Component
@Path("community-cards")
public class CommunityCardController {

    private final static int PAGE_SIZE = 10;
    @Autowired
    private SearchService ss;

    @Context
    private UriInfo uriInfo;

    @Autowired
    private UserService us;

    @Autowired
    private CommunityService cs;

    @Autowired
    private Commons commons;

    @GET
    @Path("")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response list(@DefaultValue("1") @QueryParam("page") int page,
                         @DefaultValue("") @QueryParam("query") String query) {


        int size = PAGE_SIZE;
        int offset = (page - 1) * size;
        if(size < 1 )
            size = 1;

        List<Community> cl = ss.searchCommunity(query , size, offset);

        int total = (int) Math.ceil(ss.searchCommunityCount(query) / (double)size);

        UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();

        if(!query.equals(""))
            uriBuilder.queryParam("query" , query);

        return communityListToResponse(cl , page , total , uriBuilder);
    }

    @GET
    @Path("/askable")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response list(@DefaultValue("1") @QueryParam("page") int page,@DefaultValue("-1") @QueryParam("requestorId") int userId) {


        int size = PAGE_SIZE;
        int offset = (page - 1) * size;

        if(userId < 0){
            List<Community> cl = cs.getPublicCommunities();
            UriBuilder uri = uriInfo.getAbsolutePathBuilder();
            if(userId != -1 )
                uri.queryParam("requestorId" , userId);
            if(page != 1)
                cl = new ArrayList<>();
            return communityListToResponse(cl , 1 , 1 , uri );
        }

        User u = commons.currentUser();

        if( u == null)
            return GenericResponses.notAuthorized();
        if( userId != -1 && u.getId() != userId)
            return GenericResponses.cantAccess();

        List<Community> cl = cs.list(u.getId() , size , offset);

        int total = (int) Math.ceil(cs.listCount(u.getId()) / (double)size);
        UriBuilder uri = uriInfo.getAbsolutePathBuilder();
        if(userId != -1 )
            uri.queryParam("requestorId" , userId);
        return communityListToResponse(cl , page , total , uri );
    }

    private Community addUserCount( Community c){
        Number count = cs.getUserCount(c.getId()).orElse(0);
        c.setUserCount(count.longValue());
        return c;
    }
    @GET
    @Path("/moderated")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response getModeratedCommunities(@QueryParam("userId") @DefaultValue("-1") final long id ,@QueryParam("page") @DefaultValue("1") int page) {

        final User user = us.findById(id).orElse(null);

        if (user != null) {
            List<Community> communities = us.getModeratedCommunities( id , page -1 );
            //communities = communities.stream().map(x ->addUserCount(x) ).collect(Collectors.toList());
            int pages = (int) us.getModeratedCommunitiesPages(id);
            UriBuilder uri = uriInfo.getAbsolutePathBuilder();
            if(id != -1 )
                uri.queryParam("userdId" , id );

            return communityListToResponse(communities , page , pages , uri);


        } else {
            return GenericResponses.notFound();
        }
    }


    @GET
    @Path("/admitted")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response getAdmittedCommunities(@QueryParam("page") @DefaultValue("1") int page , @QueryParam("requestorId") @DefaultValue("-1") int userId) {
        return  getInvitedByAccessLevel(page , userId , AccessType.ADMITTED);
    }
    @GET
    @Path("/requested")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response getRequestedCommunities(@QueryParam("page") @DefaultValue("1") int page , @QueryParam("requestorId") @DefaultValue("-1") int userId) {
        return  getInvitedByAccessLevel(page , userId , AccessType.REQUESTED);
    }

    @GET
    @Path("/request-rejected")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response getRequestRejected(@QueryParam("page") @DefaultValue("1") int page , @QueryParam("requestorId") @DefaultValue("-1") int userId) {
        return  getInvitedByAccessLevel(page , userId , AccessType.REQUEST_REJECTED);
    }
    @GET
    @Path("/invited")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response getInvitedCommunities(@QueryParam("page") @DefaultValue("1") int page , @QueryParam("requestorId") @DefaultValue("-1") int userId) {
        return  getInvitedByAccessLevel(page , userId , AccessType.INVITED);
    }
    @GET
    @Path("/invite-rejected")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response getInviteRejectedCommunities(@QueryParam("page") @DefaultValue("1") int page , @QueryParam("requestorId") @DefaultValue("-1") int userId) {
        return  getInvitedByAccessLevel(page , userId , AccessType.INVITE_REJECTED);
    }
    @GET
    @Path("/left")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response getLeftCommunities(@QueryParam("page") @DefaultValue("1") int page , @QueryParam("requestorId") @DefaultValue("-1") int userId) {
        return  getInvitedByAccessLevel(page , userId , AccessType.LEFT);
    }

    @GET
    @Path("/blocked")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response getBlockedCommunities(@QueryParam("page") @DefaultValue("1") int page ,@QueryParam("requestorId") @DefaultValue("1") int userId ) {
        return  getInvitedByAccessLevel(page , userId , AccessType.BLOCKED_COMMUNITY);
    }

    @GET
    @Path("/kicked")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response getKickedCommunities(@QueryParam("page") @DefaultValue("1") int page ,@QueryParam("requestorId") @DefaultValue("1") int userId ) {
        return  getInvitedByAccessLevel(page , userId , AccessType.KICKED);
    }


    @GET
    @Path("/banned")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response getBanned(@QueryParam("page") @DefaultValue("1") int page ,@QueryParam("requestorId") @DefaultValue("1") int userId ) {
        return  getInvitedByAccessLevel(page , userId , AccessType.BANNED);
    }



    private Response getInvitedByAccessLevel(int page , int userId , AccessType accessType){

        final User u = commons.currentUser();

        if(page < 1 )
            return GenericResponses.badRequest();

        if (u == null ) {
            //TODO mejores errores
            return GenericResponses.notAuthorized();
        }
        if( u.getId() != userId)
            return GenericResponses.cantAccess();

        int pageSize = 5;
        int pages = (int) us.getCommunitiesByAccessTypePages(userId,  accessType);
        List<Community> communities = us.getCommunitiesByAccessType(userId, accessType, page -1 );

        UriBuilder uri = uriInfo.getBaseUriBuilder();
        if( userId != -1)
            uri.queryParam("requestorId" , userId);

        return communityListToResponse(communities , page , pages , uri);

    }



    private Response communityListToResponse(List<Community> cl , int page , int pages , UriBuilder uri){

        cl = cl.stream().map(this::addUserCount).collect(Collectors.toList());
        List<CommunityCardDto> cldto = cl.stream().map( x-> CommunityCardDto.toCommunityCard(x,uriInfo)).collect(Collectors.toList());
        Response.ResponseBuilder res =  Response.ok(
                new GenericEntity<List<CommunityCardDto>>(cldto) {
                }
        );
        return PaginationHeaderUtils.addPaginationLinks(page , pages , uri , res);

    }
}
