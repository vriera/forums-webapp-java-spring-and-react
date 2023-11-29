package ar.edu.itba.paw.webapp.controller;
import ar.edu.itba.paw.interfaces.services.CommunityService;
import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.exceptions.EmailAlreadyExistsException;
import ar.edu.itba.paw.models.exceptions.IncorrectPasswordException;
import ar.edu.itba.paw.models.exceptions.UsernameAlreadyExistsException;
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
    @Path("/")
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

        User createdUser;

        try{
        createdUser = us.create(userForm.getUsername(), userForm.getEmail(), userForm.getPassword(), baseUrl);
        } catch (UsernameAlreadyExistsException e) {
            return GenericResponses.conflict(GenericResponses.USERNAME_ALREADY_EXISTS , "Another user is already registered with the given username");
        }
        catch (EmailAlreadyExistsException e) {
            return GenericResponses.conflict(GenericResponses.EMAIL_ALREADY_EXISTS , "Another user is already registered with the given email");
        }



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
                new GenericEntity<UserDto>(UserDto.userToUserDto(user, uriInfo)){}
        ).build();
    }


    @PUT
    @Path("/{id}")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response update( @Valid final UpdateUserForm userForm , @PathParam("id") int id){
        final User currentUser =  commons.currentUser();

        User updatedUser;

        updatedUser = us.update(currentUser, userForm.getNewUsername(), userForm.getNewPassword(), userForm.getCurrentPassword() );


        return Response.ok(
                new GenericEntity<UserDto>(UserDto.userToUserDto(updatedUser, uriInfo)){}
        ).build();
    }

    @GET
    @Path("/{userId}/communities/{communityId} ")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response getUsersByAccessType( @PathParam("userId") final long userId , @PathParam("communityId") final long communityId , @DefaultValue("1") @QueryParam("page") final int page , @DefaultValue("admitted") @QueryParam("accessType") final String accessTypeString){
        LOGGER.info("Getting users by access type {} for community {} and requester {}", accessTypeString , communityId , userId);

        // This may throw an IllegalAccessException, which will be mapped to a BadRequest response
        AccessType accessType = AccessType.valueOf(accessTypeString.toLowerCase());

        int pages = (int) cs.getMembersByAccessTypePages(communityId, accessType);

        List<User> ul = cs.getMembersByAccessType(communityId, accessType, page - 1);

        UriBuilder uri = uriInfo.getAbsolutePathBuilder();
        uri.queryParam("accessType" , accessTypeString);

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