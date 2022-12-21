package ar.edu.itba.paw.webapp.controller;
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
    public Response searchUsers(@QueryParam("page") @DefaultValue("1") int page , @QueryParam("query") @DefaultValue("") String query) {
        int size = 10;
        int offset = size * (page -1);

        LOGGER.debug("LOGGER: Getting all the users");
        final List<User> allUsers = ss.searchUser(query , size ,offset);
        int count = ss.searchUserCount(query);
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
    public Response createUser(@Valid final UserForm userForm) { //chequear metodo

        if( userForm.getEmail() == null || userForm.getPassword() ==null || userForm.getRepeatPassword() ==null || userForm.getUsername() ==null)
            return GenericResponses.badRequest();

        final Optional<User> u = us.findByEmail(userForm.getEmail());
        if(u.isPresent())
            return GenericResponses.conflict("email.already.exists" , "Another user is already registered with the given email");

        if(!userForm.getRepeatPassword().equals(userForm.getPassword()))
            return GenericResponses.badRequest("passwords.do.not.match","Passwords do not match");

        final String baseUrl = uriInfo.getBaseUriBuilder().replacePath(servletContext.getContextPath()).toString();
        final Optional<User> user = us.create(userForm.getUsername(), userForm.getEmail(), userForm.getPassword(),baseUrl);

        if(!user.isPresent()){
            return Response.status(Response.Status.CONFLICT).build();
        }

        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path(String.valueOf(user.get().getId())).build();  //chequear si esta presente

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

//    //Information user
//    @GET
//    @Path("/questions")
//    @Produces(value = {MediaType.APPLICATION_JSON})
//    public Response userQuestions(@DefaultValue("0") @QueryParam("page") final int page){
//
//        User u = commons.currentUser();
//        if(u == null){
//            //TODO mejores errores
//            return GenericResponses.notAuthorized();
//        }
//
//        List<Question> ql = us.getQuestions(u.getId() , page);
//        DashboardQuestionListDto qlDto = DashboardQuestionListDto.questionListToQuestionListDto(ql , uriInfo , page , 5 ,us.getPageAmountForQuestions(u.getId()));
//
//        return Response.ok(
//                new GenericEntity<DashboardQuestionListDto>(qlDto){}
//        ).build();
//
//    }

//
//    @GET
//    @Path("/answers")
//    @Produces(value = {MediaType.APPLICATION_JSON})
//    public Response userAnswers(@DefaultValue("0") @QueryParam("page") int page){
//
//        User u = commons.currentUser();
//        if( u == null ){
//            //TODO mejores errores
//            return GenericResponses.notAuthorized();
//        }
//
//        List<Answer> al = us.getAnswers(u.getId() , page);
//        DashboardAnswerListDto alDto = DashboardAnswerListDto.answerListToQuestionListDto(al , uriInfo , page , 5 ,us.getPageAmountForAnswers(u.getId()));
//
//        return Response.ok(
//                new GenericEntity<DashboardAnswerListDto>(alDto){}
//        ).build();
//
//    }



    @PUT
    @Path("/{id}")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response modifyUserInfo( @Valid final UpdateUserForm userForm , @PathParam("id") int id){

        final User user =  commons.currentUser();

        if( user == null){
            //TODO mejores errores
            return GenericResponses.notAuthorized();
        }
        if(user.getId() != id){
            return GenericResponses.cantAccess();
        }
        //TODO errores mas papota
        if(userForm.getCurrentPassword() == null || userForm.getNewPassword() ==null|| userForm.getNewUsername() ==null ){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        if(!us.passwordMatches(userForm.getCurrentPassword() , user)){
            return GenericResponses.notAuthorized("not.question.owner" , "User must be question owner to verify the answer");
        }
        String username = userForm.getNewUsername();
        String password = userForm.getCurrentPassword();
        String newPassword = userForm.getNewPassword();
        us.updateUser(user , password , newPassword , username);

        final Optional<User> u = us.findById(user.getId());
        if( !u.isPresent()){
            return Response.noContent().build();
        }
        u.get().setUsername(username);
        return Response.ok(
                new GenericEntity<UserDto>(UserDto.userToUserDto(u.get(), uriInfo)){}
        ).build();
    }
    /*
    @DELETE
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response deleteById(@PathParam("id") final long id) {
        us.deleteById(id);
        return Response.noContent().build();
    }

     */

    //Falta verificacion

    private boolean canAuthorize(long communityId, long authorizerId){
        Optional<Community> maybeCommunity = cs.findById(communityId);

        // Si el autorizador no es el moderador, no tiene acceso a la acción
        return maybeCommunity.isPresent() && authorizerId == maybeCommunity.get().getModerator().getId();
    }
    private boolean canInteract(long userId, long authorizerId){
        return  authorizerId == userId;
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
    @Path("/banned")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response bannedUsers(@QueryParam("communityId") @DefaultValue("-1") final int communityId , @DefaultValue("-1")  @QueryParam("moderatorId") final int userId , @DefaultValue("1")  @QueryParam("page") final int page    ){
        return getUserByAccessType(communityId , page , userId , AccessType.BANNED);
    }
    private Response getUserByAccessType(int communityId , int page , int userId ,AccessType accessType){
        final User u = commons.currentUser();
        Optional<Community> community = cs.findById(communityId);
        
        if(!community.isPresent())
            return GenericResponses.notFound();

        if(community.get().getModerator().getId() == 0)
            return GenericResponses.badRequest("community.is.public" , "The community is public");

        if( u == null){
            return GenericResponses.notAuthorized();
        }

        if ( u.getId() != userId){
            return GenericResponses.cantAccess();
        }
        if(communityId < 1)
            return GenericResponses.badRequest();

        if(page < 1)
            return GenericResponses.badRequest();

        if(!us.isModerator(u.getId() , communityId)){
            return GenericResponses.cantAccess();
        }


        int pages = (int) cs.getMemberByAccessTypePages(communityId, accessType);

        List<User> ul = cs.getMembersByAccessType(communityId,accessType, page - 1);

        UriBuilder uri = uriInfo.getAbsolutePathBuilder();
        uri.queryParam("moderatorId" , userId );
        return userListToResponse(ul , page , pages , uri);

    }

    private Response userListToResponse( List<User> ul , int page , int pages , UriBuilder uri){

        List<UserDto> userDtoList = ul.stream().map(x -> UserDto.userToUserDto(x ,uriInfo)).collect(Collectors.toList());

        Response.ResponseBuilder res = Response.ok(
                new GenericEntity<List<UserDto>>(userDtoList){}
        );
        return PaginationHeaderUtils.addPaginationLinks(page, pages,uri , res);
    }






}