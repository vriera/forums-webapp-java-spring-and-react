package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CommunityService;
import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.exceptions.EmailAlreadyExistsException;
import ar.edu.itba.paw.models.exceptions.UsernameAlreadyExistsException;
import ar.edu.itba.paw.webapp.controller.utils.GenericResponses;
import ar.edu.itba.paw.webapp.dto.input.UserUpdateDto;
import ar.edu.itba.paw.webapp.dto.output.KarmaDto;
import ar.edu.itba.paw.webapp.dto.output.NotificationDto;
import ar.edu.itba.paw.webapp.dto.output.UserDto;
import ar.edu.itba.paw.webapp.controller.utils.PaginationHeaderUtils;
import ar.edu.itba.paw.webapp.dto.input.UserCreateDto;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.ServletContext;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Path("users")
@Component
@Validated
public class UserController {
    @Autowired
    private UserService us;

    @Autowired
    private CommunityService cs;

    @Context
    private UriInfo uriInfo;

    @Autowired
    private Commons commons;

    @Autowired
    private ServletContext servletContext;

    @Autowired
    private SearchService ss;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    // Information global
    @GET
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response searchUsers(@QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("query") String query,
            @QueryParam("email") String email,
            @QueryParam("accessType") AccessType accessType,
            @QueryParam("communityId") Long communityId) {

        final List<User> users = ss.searchUser(query, email, accessType, communityId, page - 1);

        long pages = ss.searchUserPagesCount(query, email, accessType, communityId);

        return userListToResponse(users, page, (int) pages, uriInfo.getAbsolutePathBuilder(),
                uriInfo.getQueryParameters());
    }

    // TODO: Mejorar este endpoint
    @POST
    @Consumes(value = { MediaType.APPLICATION_JSON })
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response createUser(@Valid @RequestBody @NotNull final UserCreateDto userForm) {

        final String baseUrl = uriInfo.getBaseUriBuilder().replacePath(servletContext.getContextPath()).toString();

        User createdUser;

        createdUser = us.create(userForm.getUsername(), userForm.getEmail(), userForm.getPassword(), baseUrl);


        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(createdUser.getId())).build();

        return Response.created(uri).build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response getById(@PathParam("id") final long id) {
        User user = us.findById(id);

        return Response.ok(
                new GenericEntity<UserDto>(UserDto.userToUserDto(user, uriInfo)) {
                }).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(value = { MediaType.APPLICATION_JSON })
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response update(@Valid final UserUpdateDto userForm, @PathParam("id") int id) {
        final User currentUser = commons.currentUser();

        User updatedUser;

        updatedUser = us.update(currentUser, userForm.getNewUsername(), userForm.getNewPassword(),
                userForm.getCurrentPassword());

        return Response.ok(
                new GenericEntity<UserDto>(UserDto.userToUserDto(updatedUser, uriInfo)) {
                }).build();
    }

    private Response userListToResponse(List<User> ul, int page, int pages, UriBuilder uri,
            MultivaluedMap<String, String> params) {

        List<UserDto> userDtoList = ul.stream().map(x -> UserDto.userToUserDto(x, uriInfo))
                .collect(Collectors.toList());

        if (userDtoList.isEmpty())
            Response.noContent().build();
        Response.ResponseBuilder res = Response.ok(
                new GenericEntity<List<UserDto>>(userDtoList) {
                });
        return PaginationHeaderUtils.addPaginationLinks(page, pages, uri, res, params);
    }

    /*
     * Karma
     */
    @GET
    @Path("/{id}/karma")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response getQuestion(@PathParam("id") final Long id) {
        Karma karma = us.getKarma(id);
        KarmaDto karmaDto = KarmaDto.KarmaToKarmaDto(karma, uriInfo);
        return Response.ok(new GenericEntity<KarmaDto>(karmaDto) {
        })
                .build();
    }

    /*
     * Notifications
     */

    @GET
    @Path("/{userId}/notifications")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response getNotification(@PathParam("userId") int userId) {
        User u = commons.currentUser();
        final Notification notifications = us.getNotifications(u.getId());

        NotificationDto notificationsDto = NotificationDto.notificationToNotificationDto(notifications, uriInfo);
        return Response.ok(new GenericEntity<NotificationDto>(notificationsDto) {
        }).build();
    }
}