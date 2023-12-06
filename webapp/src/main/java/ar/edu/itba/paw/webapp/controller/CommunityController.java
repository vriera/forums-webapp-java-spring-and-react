package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.interfaces.services.CommunityService;
import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.auth.PawUserDetailsService;
import ar.edu.itba.paw.webapp.controller.utils.GenericResponses;
import ar.edu.itba.paw.webapp.controller.utils.PaginationHeaderUtils;
import ar.edu.itba.paw.webapp.dto.input.AccessDto;
import ar.edu.itba.paw.webapp.dto.input.CommunityCreateDto;
import ar.edu.itba.paw.webapp.dto.output.AccessInfoDto;
import ar.edu.itba.paw.webapp.dto.output.CommunityDto;
import ar.edu.itba.paw.webapp.dto.output.CommunityNotificationsDto;
import ar.edu.itba.paw.webapp.dto.validation.ValidAccessType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Validated
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
                         @DefaultValue("") @QueryParam("query") String query,
                         @QueryParam("accessType") @Valid AccessType accessType,
                         @QueryParam("userId") Integer userId,
                         @QueryParam("moderatorId") Integer moderatorId) {

        System.out.println("ACCESS TYPE: "  + accessType);
        int size = 10;
        int offset = (page - 1) * size;

        List<Community> cl = ss.searchCommunity(query, accessType, moderatorId , userId , size, offset);

        if (cl.isEmpty()) return Response.noContent().build();

        int total = (int) Math.ceil(ss.searchCommunityCount(query, accessType , moderatorId , userId ) / (double) size);

        return communityListToResponse(cl, page, total, uriInfo.getAbsolutePathBuilder() , uriInfo.getQueryParameters());
    }

    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getCommunity(@PathParam("id") int id) {
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
    public Response create(@Valid final CommunityCreateDto communityForm) {
        final User u = commons.currentUser();
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
    public Response modifyAccessType(@Valid AccessDto accessDto, @PathParam("userId") final long userId, @PathParam("communityId") final long communityId){
        cs.modifyAccessType(userId, communityId, AccessType.valueOf(accessDto.getAccessType()));
        return GenericResponses.success();
    }




    private Response communityListToResponse(List<Community> cl, int page, int pages, UriBuilder uri , MultivaluedMap<String,String> params) {

        if (cl.isEmpty()) return Response.noContent().build();
        List<CommunityDto> cldto = cl.stream().map(x -> CommunityDto.communityToCommunityDto(x, uriInfo)).collect(Collectors.toList());
        Response.ResponseBuilder res = Response.ok(
                new GenericEntity<List<CommunityDto>>(cldto) {
                }
        );
        return PaginationHeaderUtils.addPaginationLinks(page, pages, uri, res , params);

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



