package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CommunityService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.controller.utils.GenericResponses;
import ar.edu.itba.paw.webapp.dto.CommunityListDto;
import ar.edu.itba.paw.webapp.dto.DashboardAnswerListDto;
import ar.edu.itba.paw.webapp.dto.DashboardQuestionListDto;
import ar.edu.itba.paw.webapp.dto.UserDto;
import ar.edu.itba.paw.webapp.form.UpdateUserForm;
import org.apache.commons.logging.Log;
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


    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    //Information global
    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response listUsers(@QueryParam("page") @DefaultValue("1") int page) {
        LOGGER.debug("LOGGER: Getting all the users");
        final List<UserDto> allUsers = us.getUsers(page).stream().map(user -> UserDto.userToUserDto(user,uriInfo)).collect(Collectors.toList());
        return Response.ok(new GenericEntity<List<UserDto>>(allUsers){})
                .build();
    }

    @POST
    @Consumes(value = { MediaType.APPLICATION_JSON, })
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response createUser(@Valid final UserDto userDto) { //chequear metodo


        final Optional<User> user = us.create(userDto.getUsername(), userDto.getEmail(), userDto.getPassword());

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
    //Information user
    @GET
    @Path("/{id}/questions")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response userQuestions(@PathParam("id") final int id , @DefaultValue("1") @QueryParam("page") final int page){

        User u = commons.currentUser();
        if( u.getId() != id){
            //TODO mejores errores
            return GenericResponses.notAuthorized();
        }

        List<Question> ql = us.getQuestions(u.getId() , page);
        DashboardQuestionListDto qlDto = DashboardQuestionListDto.questionListToQuestionListDto(ql , uriInfo , page , 5 ,us.getPageAmountForQuestions(u.getId()));

        return Response.ok(
                new GenericEntity<DashboardQuestionListDto>(qlDto){}
        ).build();

    }


    @GET
    @Path("/{id}/answers")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response userAnswers(@PathParam("id") final int id ,@DefaultValue("1") @QueryParam("page") int page){

        User u = commons.currentUser();
        if( u.getId() != id){
            //TODO mejores errores
            return GenericResponses.notAuthorized();
        }

        List<Answer> al = us.getAnswers(u.getId() , page);
        DashboardAnswerListDto alDto = DashboardAnswerListDto.answerListToQuestionListDto(al , uriInfo , page , 5 ,us.getPageAmountForAnswers(u.getId()));

        return Response.ok(
                new GenericEntity<DashboardAnswerListDto>(alDto){}
        ).build();

    }
    //

    @GET
    @Path("/{id}/moderated")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response getModeratedCommunities(@PathParam("id") final long id ,@QueryParam("page") @DefaultValue("1") int page) {
        final User user = us.findById(id).orElse(null);

        if (user != null) {
            List<Community> communities = us.getModeratedCommunities( id , page - 1);
            CommunityListDto cldto = CommunityListDto.CommunityListToCommunityListDto(communities , uriInfo , null , page , 5 , (int) us.getModeratedCommunitiesPages(id) );
            return Response.ok(
                    new GenericEntity<CommunityListDto>(cldto){}
            ).build();

        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @PUT
    @Path("/{id}/update/")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response modifyUserInfo(@PathParam("id") final long id , @Valid final UpdateUserForm userForm){

        final User user =  commons.currentUser();
        if( user.getId() != id){
            //TODO mejores errores
            return GenericResponses.notAuthorized();
        }
        //TODO errores mas papota
        if(userForm.getCurrentPassword() == null || userForm.getNewPassword() ==null|| userForm.getNewUsername() ==null ){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        if(!us.passwordMatches(userForm.getCurrentPassword() , user)){
            return Response.status(Response.Status.BAD_REQUEST).build();
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


    @PUT
    @Path("/{authorizerId}/community/{communityId}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response access(@QueryParam("accessType") String accessTypeParam , @QueryParam("targetUserId") final long userId, @PathParam("communityId") final long communityId, @PathParam("authorizerId") final long authorizerId){
        LOGGER.info("User {} tried to access community {} with target user {} and desired access type {}" , authorizerId, communityId, userId, accessTypeParam);

        final User currentUser = commons.currentUser();
        if(currentUser == null || currentUser.getId() != authorizerId){
            return GenericResponses.notAuthorized();
        }

        boolean success = false;
        LOGGER.debug("canInteract = {}, canAuthorize = {}", canInteract(userId, authorizerId), canAuthorize(communityId, authorizerId));

        AccessType desiredAccessType;
        try{
            desiredAccessType = AccessType.valueOf(accessTypeParam);
        }
        catch(IllegalArgumentException e){
            if(accessTypeParam.equals("NONE")){
                Optional<AccessType> currentAccess = cs.getAccess(userId, communityId);
                LOGGER.debug("Entrando al switch con access {}", "NONE");
                // Both these operations result in a reset of interactions between user and community
                if(currentAccess.isPresent() && currentAccess.get() == AccessType.BLOCKED_COMMUNITY){
                    if(!canInteract(userId, authorizerId)){
                        return GenericResponses.notAuthorized();
                    }
                    success = cs.unblockCommunity(userId, communityId);
                }
                else if(currentAccess.isPresent() && currentAccess.get() == AccessType.BANNED){
                    if(!canAuthorize(communityId, authorizerId)){
                        return GenericResponses.notAuthorized();
                    }
                    success = cs.liftBan(userId, communityId, authorizerId);
                }
            }
            return success? GenericResponses.success() : GenericResponses.badRequest();
        }

        LOGGER.debug("Entrando al switch con access {}", desiredAccessType);

        switch (desiredAccessType) {
            case ADMITTED: {
                if (canAuthorize(communityId, authorizerId)) {
                    success = cs.admitAccess(userId, communityId, authorizerId);
                } else if (canInteract(userId, authorizerId)) {
                    success = cs.acceptInvite(userId, authorizerId);
                } else {
                    return GenericResponses.notAuthorized();
                }

                break;
            }
            case KICKED: {
                if (!canAuthorize(communityId, authorizerId)) {
                    return GenericResponses.notAuthorized();
                }
                success = cs.kick(userId, communityId, authorizerId);

                break;
            }
            case BANNED: {
                if (!canAuthorize(communityId, authorizerId)) {
                    return GenericResponses.notAuthorized();
                }
                success = cs.ban(userId, communityId, authorizerId);

                break;
            }
            case REQUEST_REJECTED: {
                if (!canAuthorize(communityId, authorizerId)) {
                    return GenericResponses.notAuthorized();
                }
                success = cs.rejectAccess(userId, communityId, authorizerId);

                break;
            }
            case INVITED: {
                if (!canAuthorize(communityId, authorizerId)) {
                    return GenericResponses.notAuthorized();
                }
                success = cs.invite(userId, communityId, authorizerId);

                break;
            }
            case REQUESTED: {
                if (!canInteract(userId, authorizerId)) {
                    return GenericResponses.notAuthorized();
                }
                success = cs.requestAccess(userId, communityId);

                break;
            }
            case INVITE_REJECTED: {
                if (!canInteract(userId, authorizerId)) {
                    return GenericResponses.notAuthorized();
                }
                success = cs.refuseInvite(userId, communityId);

                break;
            }
            case LEFT: {
                if (!canInteract(userId, authorizerId)) {
                    return GenericResponses.notAuthorized();
                }
                success = cs.leaveCommunity(userId, communityId);

                break;
            }
            case BLOCKED_COMMUNITY: {
                if (!canInteract(userId, authorizerId)) {
                    return GenericResponses.notAuthorized();
                }
                success = cs.blockCommunity(userId, communityId);

                break;
            }
        }

        return success? GenericResponses.success() : GenericResponses.badRequest();

    }

}