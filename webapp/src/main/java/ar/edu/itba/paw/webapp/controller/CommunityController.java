package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.interfaces.services.CommunityService;
import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.AccessType;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.CommunityNotifications;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.exceptions.InvalidAccessTypeChangeException;
import ar.edu.itba.paw.webapp.controller.dto.*;
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
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Path("communities")
public class CommunityController {
    private final static int PAGE_SIZE = 10;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private CommunityService cs;
    @Autowired
    private UserService us;
    @Autowired
    private SearchService ss;
    @Context
    private UriInfo uriInfo;
    @Autowired
    private Commons commons;

    @GET
    @Path("/")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response list(@DefaultValue("1") @QueryParam("page") int page,
                         @DefaultValue("") @QueryParam("query") String query) {

        int size = PAGE_SIZE;
        int offset = (page - 1) * size;
        if (size < 1)
            size = 1;

        List<Community> cl = ss.searchCommunity(query, size, offset);
        if (cl.isEmpty()) return Response.noContent().build();

        int total = (int) Math.ceil(ss.searchCommunityCount(query) / (double) size);

        UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();

        if (!query.equals(""))
            uriBuilder.queryParam("query", query);

        return communityListToResponse(cl, page, total, uriBuilder);
    }

    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getCommunity(@PathParam("id") int id) {
        // FIXME: Lógica de negocios!
        if (id < 0) //return GenericResponses.badRequest("illegal.id" , "Id cannot be negative");
            throw new IllegalArgumentException();


        Community community = cs.findById(id);
        CommunityDto cd = CommunityDto.communityToCommunityDto(community, uriInfo);

        return Response.ok(
                new GenericEntity<CommunityDto>(cd) {
                }
        ).build();
    }

    @POST
    @Path("/")
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response create(@Valid final CommunityForm communityForm) {
        final User u = commons.currentUser();


        //Name
        //Description
        //TODO: the validations --> VAN EN LOS SERVICIOS NO ACA
//        if(cs.findByName(communityForm.getName()).isPresent())
//            return GenericResponses.conflict("community.name.taken" , "A community with the given name already exists");


        final String title = communityForm.getName();
        final String description = communityForm.getDescription();
        Community c = cs.create(title, description, u);

        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(c.getId())).build();
        return Response.created(uri).build();
    }

    @GET
    @Path("/{communityId}/users/{userId}/accessType")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getAccessType(@PathParam("userId") final long userId, @PathParam("communityId") final long communityId) {
        Community c = cs.findById(communityId);

        if (c.getModerator().getId() == 0) {
            return Response.ok(new GenericEntity<AccessInfoDto>(AccessInfoDto.noTypeAccessInfoDto(true, communityId, userId, uriInfo)) {
            }).build();
        }

        User u = us.findById(userId);
        Boolean access = cs.canAccess(u, c);
        AccessType accessType = cs.getAccess(userId, communityId);
        return Response.ok(new GenericEntity<AccessInfoDto>(AccessInfoDto.acessTypeToAccessInfoDto(access, accessType, communityId, userId, uriInfo)) {
        }).build();
    }

    @PUT
    @Path("/{communityId}/users/{userId}/accessType")
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response modifyAccessType(@Valid AccessDto accessDto, @PathParam("userId") final long userId, @PathParam("communityId") final long communityId) throws InvalidAccessTypeChangeException {

        AccessType targetAccessType;
        try {
            targetAccessType = AccessType.valueOf(accessDto.getAccessType());
        } catch (IllegalArgumentException e){
            throw new IllegalArgumentException("No such access type: " + accessDto.getAccessType());
        }

        cs.modifyAccessType(userId, communityId, targetAccessType);

        return GenericResponses.success();
    }

    @GET
    @Path("/askable") //TODO: pasar esto a SPRING SECURITY
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response list(@DefaultValue("1") @QueryParam("page") int page, @DefaultValue("-1") @QueryParam("userId") int userId) {


        int size = PAGE_SIZE;
        int offset = (page - 1) * size;
        //TODO FALTA not found
        if (userId < 0) {
            List<Community> cl = cs.getPublicCommunities();
            UriBuilder uri = uriInfo.getAbsolutePathBuilder();
            if (userId != -1)
                uri.queryParam("userId", userId);
            if (page != 1)
                cl = new ArrayList<>();
            return communityListToResponse(cl, 1, 1, uri);
        }

        User u = commons.currentUser();

        if (u == null)
            return GenericResponses.notAuthorized();
        if (userId != -1 && u.getId() != userId)
            return GenericResponses.cantAccess();

        List<Community> cl = cs.list(u.getId(), size, offset);

        int total = (int) Math.ceil(cs.listCount(u.getId()) / (double) size);
        UriBuilder uri = uriInfo.getAbsolutePathBuilder();
        if (userId != -1)
            uri.queryParam("userId", userId);
        return communityListToResponse(cl, page, total, uri);
    }

    @GET
    @Path("/moderated")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response getModeratedCommunities(@QueryParam("userId") @DefaultValue("-1") final long id, @QueryParam("page") @DefaultValue("1") int page) {

        List<Community> communities = us.getModeratedCommunities(id, page - 1);
        if (communities.isEmpty()) return Response.noContent().build();
        //communities = communities.stream().map(x ->addUserCount(x) ).collect(Collectors.toList());
        int pages = (int) us.getModeratedCommunitiesPages(id);
        UriBuilder uri = uriInfo.getAbsolutePathBuilder();
        if (id != -1)
            uri.queryParam("userdId", id);

        return communityListToResponse(communities, page, pages, uri);
    }

    @GET
    @Path("/admitted")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getAdmittedCommunities(@QueryParam("page") @DefaultValue("1") int page, @QueryParam("userId") @DefaultValue("-1") int userId) {
        return getInvitedByAccessLevel(page, userId, AccessType.ADMITTED);
    }

    @GET
    @Path("/requested")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getRequestedCommunities(@QueryParam("page") @DefaultValue("1") int page, @QueryParam("userId") @DefaultValue("-1") int userId) {
        return getInvitedByAccessLevel(page, userId, AccessType.REQUESTED);
    }

    @GET
    @Path("/request-rejected")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getRequestRejected(@QueryParam("page") @DefaultValue("1") int page, @QueryParam("userId") @DefaultValue("-1") int userId) {
        return getInvitedByAccessLevel(page, userId, AccessType.REQUEST_REJECTED);
    }

    @GET
    @Path("/invited")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getInvitedCommunities(@QueryParam("page") @DefaultValue("1") int page, @QueryParam("userId") @DefaultValue("-1") int userId) {
        return getInvitedByAccessLevel(page, userId, AccessType.INVITED);
    }

    @GET
    @Path("/invite-rejected")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getInviteRejectedCommunities(@QueryParam("page") @DefaultValue("1") int page, @QueryParam("userId") @DefaultValue("-1") int userId) {
        return getInvitedByAccessLevel(page, userId, AccessType.INVITE_REJECTED);
    }

    @GET
    @Path("/left")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getLeftCommunities(@QueryParam("page") @DefaultValue("1") int page, @QueryParam("userId") @DefaultValue("-1") int userId) {
        return getInvitedByAccessLevel(page, userId, AccessType.LEFT);
    }

    @GET
    @Path("/blocked")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getBlockedCommunities(@QueryParam("page") @DefaultValue("1") int page, @QueryParam("userId") @DefaultValue("1") int userId) {
        return getInvitedByAccessLevel(page, userId, AccessType.BLOCKED);
    }

    @GET
    @Path("/kicked")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getKickedCommunities(@QueryParam("page") @DefaultValue("1") int page, @QueryParam("userId") @DefaultValue("1") int userId) {
        return getInvitedByAccessLevel(page, userId, AccessType.KICKED);
    }


    @GET
    @Path("/banned") //TODO: pasar esto a SPRING SECURITY
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getBanned(@QueryParam("page") @DefaultValue("1") int page, @QueryParam("userId") @DefaultValue("1") int userId) {
        return getInvitedByAccessLevel(page, userId, AccessType.BANNED);
    }

    private Response getInvitedByAccessLevel(int page, int userId, AccessType accessType) {

        final User u = commons.currentUser();

        if (page < 1)
            return GenericResponses.badRequest();

        int pageSize = 5;
        int pages = (int) us.getCommunitiesByAccessTypePages(userId, accessType);
        List<Community> communities = us.getCommunitiesByAccessType(userId, accessType, page - 1);
        if (communities.isEmpty()) return Response.noContent().build();
        UriBuilder uri = uriInfo.getBaseUriBuilder();
        if (userId != -1)
            uri.queryParam("userId", userId);

        return communityListToResponse(communities, page, pages, uri);

    }

    private Response communityListToResponse(List<Community> cl, int page, int pages, UriBuilder uri) {

        if (cl.isEmpty()) return Response.noContent().build();
        List<CommunityDto> cldto = cl.stream().map(x -> CommunityDto.communityToCommunityDto(x, uriInfo)).collect(Collectors.toList());
        Response.ResponseBuilder res = Response.ok(
                new GenericEntity<List<CommunityDto>>(cldto) {
                }
        );
        return PaginationHeaderUtils.addPaginationLinks(page, pages, uri, res);

    }

    @GET
    @Path("/{communityId}/notifications")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response getNotificationOnCommunity(@PathParam("communityId") int communityId) {

        CommunityNotifications notifications = cs.getCommunityNotificationsById(communityId);
        CommunityNotificationsDto cnDto = CommunityNotificationsDto.toNotificationDtio(notifications, uriInfo);
        return Response.ok(
                new GenericEntity<CommunityNotificationsDto>(cnDto) {
                }
        ).build();
    }
}



