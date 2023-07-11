package ar.edu.itba.paw.webapp.controller;
import ar.edu.itba.paw.interfaces.services.CommunityService;
import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.exceptions.EmailTakenException;
import ar.edu.itba.paw.models.exceptions.IncorrectPasswordException;
import ar.edu.itba.paw.models.exceptions.UsernameTakenException;
import ar.edu.itba.paw.webapp.controller.utils.GenericResponses;
import ar.edu.itba.paw.webapp.controller.dto.UserDto;
import ar.edu.itba.paw.webapp.controller.utils.PaginationHeaderUtils;
import ar.edu.itba.paw.webapp.form.UpdateUserForm;
import ar.edu.itba.paw.webapp.form.UserForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.servlet.ServletContext;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("users")
@Component
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

    //Information global
    @GET
    @Path("/") //TODO: ESTA BIEN QUE LA API RETORNE A TODOS LOS USUARIOS  SIN NINGUN TIPO DE AUTH?
    @Produces(value = { MediaType.APPLICATION_JSON})
    public Response searchUsers(@QueryParam("page") @DefaultValue("1") int page , @QueryParam("query") @DefaultValue("") String query , @QueryParam("email") @DefaultValue("") String email) {
        int size = 10;
        int offset = size * (page -1);

        LOGGER.debug("LOGGER: Getting all the users");
        final List<User> allUsers = ss.searchUser(query , size ,offset, email);
        if(allUsers.isEmpty()) return Response.noContent().build();
        int count = ss.searchUserCount(query,email);
        int pages = (int) Math.ceil(((double)count)/size);
        UriBuilder uri = uriInfo.getAbsolutePathBuilder();
        if(!query.equals(""))
            uri.queryParam("query" , query);

        return userListToResponse(allUsers , page , pages , uri );
    }


    @POST
    @Path("/")
    @Consumes(value = { MediaType.APPLICATION_JSON, })
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createUser(@Valid final UserForm userForm) {

        final String baseUrl = uriInfo.getBaseUriBuilder().replacePath(servletContext.getContextPath()).toString();

        Optional<User> createdUser;

        try{
            createdUser = us.create(userForm.getUsername(), userForm.getEmail(), userForm.getPassword(), baseUrl);
        } catch (UsernameTakenException e) {
            return GenericResponses.conflict(GenericResponses.USERNAME_ALREADY_EXISTS , "Another user is already registered with the given username");
        }
        catch (EmailTakenException e) {
            return GenericResponses.conflict(GenericResponses.EMAIL_ALREADY_EXISTS , "Another user is already registered with the given email");
        }

        if(!createdUser.isPresent()){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(createdUser.get().getId())).build();

        return Response.created(uri).build();
    }


    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response getById(@PathParam("id") final long id) {
        final User user = us.findById(id).orElse(null);

        if (user != null) {

            return Response.ok(
                    new GenericEntity<UserDto>(UserDto.userToUserDto(user , uriInfo)){}
            ).build();

        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }


    @PUT
    @Path("/{id}")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response update( @Valid final UpdateUserForm userForm , @PathParam("id") int id){
        LOGGER.info("UPDATE USER INFO CONTROLLER: newUsername: {}, newPassword: {}, currentPassword: {}", userForm.getNewUsername(), userForm.getNewPassword(), userForm.getCurrentPassword());
        final User currentUser =  commons.currentUser();

        Optional<User> updatedUser;

        try {
            updatedUser = us.update(currentUser, userForm.getNewUsername(), userForm.getNewPassword(), userForm.getCurrentPassword() );
        } catch (IncorrectPasswordException e) {
            return GenericResponses.notAuthorized(GenericResponses.INCORRECT_CURRENT_PASSWORD , "The password is invalid");
        } catch (UsernameTakenException e) {
            return GenericResponses.conflict(GenericResponses.USERNAME_ALREADY_EXISTS , "Another user is already registered with the given username");
        }

        // Update should have returned the modified user
        if (!updatedUser.isPresent()) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        return Response.ok(
                new GenericEntity<UserDto>(UserDto.userToUserDto(updatedUser.get(), uriInfo)){}
        ).build();
    }

    //Admitted
    @GET
    @Path("/admitted")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response admittedUsers(@QueryParam("communityId") @DefaultValue("-1") final int communityId , @DefaultValue("-1")  @QueryParam("moderatorId") final int userId , @DefaultValue("1")  @QueryParam("page") final int page    ){
        return getUserByAccessType(communityId , page , userId , AccessType.ADMITTED);
    }

    //invited
    @GET
    @Path("/requested")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response inviteRequestedUsers(@QueryParam("communityId") @DefaultValue("-1") final int communityId , @DefaultValue("-1")  @QueryParam("moderatorId") final int userId , @DefaultValue("1")  @QueryParam("page") final int page    ){
        return getUserByAccessType(communityId , page , userId , AccessType.REQUESTED);
    }

    //request-rejected
    @GET
    @Path("/request-rejected")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response inviteRequestRejectedUsers(@QueryParam("communityId") @DefaultValue("-1") final int communityId , @DefaultValue("-1")  @QueryParam("moderatorId") final int userId , @DefaultValue("1")  @QueryParam("page") final int page    ){
        return getUserByAccessType(communityId , page , userId , AccessType.REQUEST_REJECTED);
    }

    //invited
    @GET
    @Path("/invited")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response invitedUser(@QueryParam("communityId") @DefaultValue("-1") final int communityId , @DefaultValue("-1")  @QueryParam("moderatorId") final int userId , @DefaultValue("1")  @QueryParam("page") final int page    ){
        return getUserByAccessType(communityId , page , userId , AccessType.INVITED);
    }

    //invite-rejected
    @GET
    @Path("/invite-rejected")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response inviteRejectedUsers(@QueryParam("communityId") @DefaultValue("-1") final int communityId , @DefaultValue("-1")  @QueryParam("moderatorId") final int userId , @DefaultValue("1")  @QueryParam("page") final int page    ){
        return getUserByAccessType(communityId , page , userId , AccessType.INVITE_REJECTED);
    }

    //left Community
    @GET
    @Path("/left")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response leftCommunity(@QueryParam("communityId") @DefaultValue("-1") final int communityId , @DefaultValue("-1")  @QueryParam("moderatorId") final int userId , @DefaultValue("1")  @QueryParam("page") final int page    ){
        return getUserByAccessType(communityId , page , userId , AccessType.LEFT);
    }

    //blocked community
    @GET
    @Path("/blocked")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response blockCommunity(@QueryParam("communityId") @DefaultValue("-1") final int communityId , @DefaultValue("-1")  @QueryParam("moderatorId") final int userId , @DefaultValue("1")  @QueryParam("page") final int page    ){
        return getUserByAccessType(communityId , page , userId , AccessType.BLOCKED_COMMUNITY);
    }

    //blocked community
    @GET
    @Path("/kicked")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response kickedFromCommunity(@QueryParam("communityId") @DefaultValue("-1") final int communityId , @DefaultValue("-1")  @QueryParam("moderatorId") final int userId , @DefaultValue("1")  @QueryParam("page") final int page    ){
        return getUserByAccessType(communityId , page , userId , AccessType.KICKED);
    }

    //Banned
    @GET
    @Path("/banned") //TODO: pasar esto a SPRING SECURITY
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response bannedUsers(@QueryParam("communityId") @DefaultValue("-1") final int communityId , @DefaultValue("-1")  @QueryParam("moderatorId") final int userId , @DefaultValue("1")  @QueryParam("page") final int page    ){
        return getUserByAccessType(communityId , page , userId , AccessType.BANNED);
    }

    private Response getUserByAccessType(int communityId , int page , int userId ,AccessType accessType){
        Optional<Community> community = cs.findById(communityId);

        if(!community.isPresent())
            return GenericResponses.notFound();

        if(community.get().getModerator().getId() == 0)
            return GenericResponses.badRequest("community.is.public" , "The community is public");

        if(communityId < 1 || userId < 1)
            return GenericResponses.badRequest();

        if(page < 1)
            return GenericResponses.badRequest();

        int pages = (int) cs.getMemberByAccessTypePages(communityId, accessType);

        List<User> ul = cs.getMembersByAccessType(communityId,accessType, page - 1);
        if(ul.isEmpty()) Response.noContent().build();
        UriBuilder uri = uriInfo.getAbsolutePathBuilder();
        uri.queryParam("moderatorId" , userId );
        return userListToResponse(ul , page , pages , uri);

    }

    private Response userListToResponse( List<User> ul , int page , int pages , UriBuilder uri){

        List<UserDto> userDtoList = ul.stream().map(x -> UserDto.userToUserDto(x ,uriInfo)).collect(Collectors.toList());
        if(userDtoList.isEmpty()) Response.noContent().build();
        Response.ResponseBuilder res = Response.ok(
                new GenericEntity<List<UserDto>>(userDtoList){}
        );
        return PaginationHeaderUtils.addPaginationLinks(page, pages,uri , res);
    }

}