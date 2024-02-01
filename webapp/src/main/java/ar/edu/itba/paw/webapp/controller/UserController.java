package ar.edu.itba.paw.webapp.controller;
import ar.edu.itba.paw.interfaces.exceptions.BadParamsException;
import ar.edu.itba.paw.interfaces.exceptions.AlreadyCreatedException;
import ar.edu.itba.paw.interfaces.exceptions.GenericBadRequestException;
import ar.edu.itba.paw.interfaces.services.CommunityService;
import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.*;
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
    public Response searchUsers(@QueryParam("page") @DefaultValue("1") int page ,
                                @QueryParam("limit") @DefaultValue("10") int limit,
                                @QueryParam("email") String email,
                                @QueryParam("query") @DefaultValue("") String query,
                                @QueryParam("communityId") Long communityId,
                                @QueryParam("accessType") @Valid AccessType accessType) throws BadParamsException {

        LOGGER.debug("LOGGER: Getting all the users");
        final List<User> allUsers = ss.searchUser(query ,accessType, communityId, email, page, limit);
        if(allUsers.isEmpty()) return Response.noContent().build();
        int count = ss.searchUserCount(query ,accessType, communityId, email);
        int pages = (int) Math.ceil(((double)count)/limit);
        UriBuilder uri = uriInfo.getAbsolutePathBuilder();
        if(!query.equals(""))
            uri.queryParam("query" , query);

        List<UserDto> userDtoList = allUsers.stream().map(x -> UserDto.userToUserDto(x ,uriInfo)).collect(Collectors.toList());
        if(userDtoList.isEmpty()) Response.noContent().build();
        Response.ResponseBuilder res = Response.ok(
                new GenericEntity<List<UserDto>>(userDtoList){}
        );
        return PaginationHeaderUtils.addPaginationLinks(page, pages,uri , res);
    }


    @POST
    @Path("/")
    @Consumes(value = { MediaType.APPLICATION_JSON, })
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createUser(@Valid final UserForm userForm) throws GenericBadRequestException {
        if(!userForm.getRepeatPassword().equals(userForm.getPassword()))
            return GenericResponses.badRequest("passwords.do.not.match","Passwords do not match");

        final String baseUrl = uriInfo.getBaseUriBuilder().replacePath(servletContext.getContextPath()).toString();
        final Optional<User> user = us.create(userForm.getUsername(), userForm.getEmail(), userForm.getPassword(),baseUrl);

        if(!user.isPresent()) return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(user.get().getId())).build();
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
    public Response modifyUserInfo( @Valid final UpdateUserForm userForm , @PathParam("id") int id){

        final User user =  commons.currentUser();
        if(!us.passwordMatches(userForm.getCurrentPassword() , user)){
            return GenericResponses.notAuthorized("not.question.owner" , "User must be question owner to verify the answer");
        }
        String username = userForm.getNewUsername();
        String password = userForm.getCurrentPassword();
        String newPassword = userForm.getNewPassword();
        us.updateUser(user , password , newPassword , username);

        final Optional<User> u = us.findById(user.getId());
        if( !u.isPresent()){
            return GenericResponses.notFound();
        }
        u.get().setUsername(username);
        return Response.ok(
                new GenericEntity<UserDto>(UserDto.userToUserDto(u.get(), uriInfo)){}
        ).build();
    }


}