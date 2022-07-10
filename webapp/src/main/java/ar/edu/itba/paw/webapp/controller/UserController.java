package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listUsers(@QueryParam("page") @DefaultValue("1") int page) {
        final List<UserDto> allUsers = us.getUsers(page).stream().map(user -> UserDto.userToUserDto(user,uriInfo)).collect(Collectors.toList());
        return Response.ok(new GenericEntity<List<UserDto>>(allUsers){})
                .build();
    }

    /*
    @GET
    @Path("single")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response getUser(@QueryParam("id") @DefaultValue("1") int id) {
        Optional<User> u = us.findById(id);
        if( !u.isPresent())
            return Response.serverError().build();
        return Response.ok(new GenericEntity<UserDto>(UserDto.userToUserDto(u.get() , uriInfo)){})
                .build();
    }*/


    @POST
    @Consumes(value = { MediaType.APPLICATION_JSON, })
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createUser(final UserDto userDto) { //chequear metodo
        final Optional<User> user = us.create(userDto.getUsername(), userDto.getEmail(), userDto.getPassword());
                final URI uri = uriInfo.getAbsolutePathBuilder()
                        .path(String.valueOf(user.get().getId())).build();  //chequear si esta presente
                return Response.created(uri).build();
        }


    /*
    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response getById(@PathParam("id") final long id) {
        final User user = us.getById(id);
        if (user != null) {
            return Response.ok(new UserDTO(user)).build();

        } else {
            return Response.status(Status.NOT_FOUND).build();
        }
    }
    @DELETE
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response deleteById(@PathParam("id") final long id) {
        us.deleteById(id);
        return Response.noContent().build();
    }

     */
}