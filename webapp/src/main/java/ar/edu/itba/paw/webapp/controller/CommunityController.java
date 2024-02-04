package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.interfaces.exceptions.AlreadyCreatedException;
import ar.edu.itba.paw.interfaces.exceptions.BadParamsException;
import ar.edu.itba.paw.interfaces.exceptions.GenericNotFoundException;
import ar.edu.itba.paw.interfaces.services.CommunityService;
import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.AccessType;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.auth.PawUserDetailsService;
import ar.edu.itba.paw.webapp.controller.dto.AccessDto;
import ar.edu.itba.paw.webapp.controller.dto.CommunityDto;
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

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response list(@DefaultValue("1") @QueryParam("page") int page,
                         @DefaultValue("5") @QueryParam("limit") final Integer limit,
                         @DefaultValue("") @QueryParam("query") String query,
                         @QueryParam("userId") Long userId,
                         @QueryParam("accessType") @Valid AccessType accessType,
                         @QueryParam("moderatorId") Long moderatorId) throws BadParamsException {

        List<Community> cl = ss.searchCommunity(query, userId, accessType, moderatorId, page, limit);
        Integer count = ss.searchCommunityCount(query, accessType, userId, moderatorId);
        if (cl.isEmpty()) return Response.noContent().build();
        int total = (int) Math.ceil(count / (double) limit);
        UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
        if (!query.equals(""))
            uriBuilder.queryParam("query", query);
        return communityListToResponse(cl, page, total, uriBuilder);
    }

    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getCommunity(@PathParam("id") Long id) throws GenericNotFoundException {
        Community community = cs.findByIdAndAddUserCount(id);
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
    public Response create(@Valid final CommunityForm communityForm) throws BadParamsException, AlreadyCreatedException {
        final User u = commons.currentUser();
        final String title = communityForm.getName();
        final String description = communityForm.getDescription();
        Optional<Community> c = cs.create(title, description, u);

        if (!c.isPresent()) return GenericResponses.badRequest();

        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(c.get().getId())).build();
        return Response.created(uri).build();
    }

    @GET
    @Path("/{communityId}/access/{userId}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getAccess(@PathParam("userId") final long userId, @PathParam("communityId") final long communityId) throws GenericNotFoundException, BadParamsException {
        //Optional<User> u = us.findById(userId);
        Optional<AccessType> accessType = cs.getAccess(userId, communityId);
        if (accessType.isPresent()) {
            AccessDto accessDto = new AccessDto();
            accessDto.setAccessType(accessType.get().name());
            accessDto.setUri(uriInfo.getBaseUriBuilder().path("/communities/").path(String.valueOf(communityId)).path("/users/").path(String.valueOf(userId)).build());
            return Response.ok(new GenericEntity<AccessDto>(accessDto) {
            }).build();
        } else return Response.noContent().build();

    }

    @PUT
    @Path("/{communityId}/access/{userId}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response access(@QueryParam("accessType") @Valid AccessType accessType, @PathParam("userId") final long userId, @PathParam("communityId") final long communityId, @QueryParam("moderatorId") final Long moderatorId) {
        boolean success = false;
        if (moderatorId != null) success = cs.setAccessByModerator(userId, communityId, accessType);
        else success = cs.setUserAccess(userId, communityId, accessType);
        return success ? Response.noContent().build() : GenericResponses.badRequest("accessType.error", "Error while performing an access action");
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


}



