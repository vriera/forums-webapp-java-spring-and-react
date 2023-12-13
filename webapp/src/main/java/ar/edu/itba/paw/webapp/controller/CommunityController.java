package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CommunityService;
import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.controller.utils.Commons;
import ar.edu.itba.paw.webapp.controller.utils.PaginationHeaderUtils;
import ar.edu.itba.paw.webapp.dto.input.AccessDto;
import ar.edu.itba.paw.webapp.dto.input.CommunityCreateDto;
import ar.edu.itba.paw.webapp.dto.output.AccessInfoDto;
import ar.edu.itba.paw.webapp.dto.output.CommunityDto;
import ar.edu.itba.paw.webapp.dto.output.CommunityNotificationsDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Validated
@Path("communities")
public class CommunityController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private CommunityService cs;

    @Autowired
    private SearchService ss;
    @Context
    private UriInfo uriInfo;
    @Autowired
    private Commons commons;



    @GET
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response list(@DefaultValue("1") @QueryParam("page") int page,
            @DefaultValue("") @QueryParam("query") String query,
            @QueryParam("accessType") @Valid AccessType accessType,
            @QueryParam("userId") Integer userId,
            @QueryParam("onlyAskable") @DefaultValue("false") boolean onlyAskable,
            @QueryParam("moderatorId") Integer moderatorId) {

        List<Community> cl = ss.searchCommunity(query, accessType, moderatorId, userId, onlyAskable, page - 1);

        if (cl.isEmpty())
            return Response.noContent().build();

        int total = (int) ss.searchCommunityPagesCount(query, accessType, moderatorId, userId, onlyAskable);

        return communityListToResponse(cl, page, total, uriInfo.getAbsolutePathBuilder(), uriInfo.getQueryParameters());
    }

    @POST
    @Produces(value = { MediaType.APPLICATION_JSON })
    @Consumes(value = { MediaType.APPLICATION_JSON })
    public Response create(@Valid final CommunityCreateDto communityForm) {
        final User u = commons.currentUser();
        final String title = communityForm.getName();
        final String description = communityForm.getDescription();
        Community c = cs.create(title, description, u);

        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(c.getId())).build();
        return Response.created(uri).build();
    }

    @GET
    @Path("/{id}")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getCommunity(@PathParam("id") long id) {
        Community community = cs.findById(id);
        CommunityDto cd = CommunityDto.communityToCommunityDto(community, uriInfo);

        return Response.ok(
                new GenericEntity<CommunityDto>(cd) {
                }).build();
    }

    /*
     * Notifications
     */
    @GET
    @Path("/{communityId}/notifications")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response getNotificationOnCommunity(@PathParam("communityId") int communityId) {

        CommunityNotifications notifications = cs.getCommunityNotificationsById(communityId);
        CommunityNotificationsDto cnDto = CommunityNotificationsDto.toNotificationDto(notifications, uriInfo);
        return Response.ok(
                new GenericEntity<CommunityNotificationsDto>(cnDto) {
                }).build();
    }

    /*
     * AccessType related
     */
    @GET
    @Path("/{communityId}/access-type/{userId}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getAccessType(@PathParam("userId") final long userId, @PathParam("communityId") final long communityId) {

        Boolean access = cs.canAccess(userId, communityId);
        AccessType accessType = cs.getAccess(userId, communityId);

        return Response.ok(new GenericEntity<AccessInfoDto>(
                AccessInfoDto.acessTypeToAccessInfoDto(access, accessType, communityId, userId, uriInfo)) {
        }).build();
    }

    @PUT
    @Path("/{communityId}/access-type/{userId}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response modifyAccessType(@Valid AccessDto accessDto, @PathParam("userId") final long userId, @PathParam("communityId") final long communityId){

        cs.modifyAccessType(userId, communityId, AccessType.valueOf(accessDto.getAccessType().toUpperCase()));

        return Response.ok().build();
    }

    /*
     * Extra methods
     */
    private Response communityListToResponse(List<Community> cl, int page, int pages, UriBuilder uri,
            MultivaluedMap<String, String> params) {

        if (cl.isEmpty())
            return Response.noContent().build();

        List<CommunityDto> cldto = cl.stream().map(x -> CommunityDto.communityToCommunityDto(x, uriInfo))
                .collect(Collectors.toList());
        Response.ResponseBuilder res = Response.ok(
                new GenericEntity<List<CommunityDto>>(cldto) {
                });

        return PaginationHeaderUtils.addPaginationLinks(page, pages, uri, res, params);

    }

}
