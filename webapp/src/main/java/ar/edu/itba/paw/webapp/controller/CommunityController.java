package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.interfaces.services.CommunityService;
import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.auth.PawUserDetailsService;
import ar.edu.itba.paw.webapp.controller.dto.*;
import ar.edu.itba.paw.webapp.controller.utils.GenericResponses;
import ar.edu.itba.paw.webapp.form.CommunityForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.Optional;

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
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getCommunity(@PathParam("id") int id ) {
        User u = commons.currentUser();
        if( u == null){
            return GenericResponses.notAuthorized();
        }
        if(id<0){
            return GenericResponses.badRequest("illegal.id" , "Id cannot be negative");
        }

        Optional<Community> c = cs.findById(id);

        if(!c.isPresent())
            return GenericResponses.notFound();

        Community community = c.get();
        community.setUserCount(0L);
        Optional<Number> uc = cs.getUserCount(id);
        if(uc.isPresent())
            community.setUserCount(uc.get().longValue());

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
    public Response create(@Valid final CommunityForm communityForm) {
        final User u = commons.currentUser();

        if( u == null ){
            //TODO mejores errores
            return GenericResponses.notAuthorized();
        }
        //Name
        //Description
        //TODO: the validations
        if(cs.findByName(communityForm.getName()).isPresent())
            return GenericResponses.conflict("community.name.taken" , "A community with the given name already exists");


        final String title = communityForm.getName();
        final String description = communityForm.getDescription();
        Optional<Community> c = cs.create(title, description, u);

        if (!c.isPresent()) {
            return GenericResponses.serverError();
        }

        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(c.get().getId())).build();
        return Response.created(uri).build();
    }

    @PUT
    @Path("/{communityId}/invite")
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response access(@Valid InviteDto inviteDto, @PathParam("communityId") final long communityId) {
//        String accessTypeParam = accessDto.getAccessType();
        final User currentUser = commons.currentUser();
        if(currentUser == null){
            return GenericResponses.notAuthorized("not.logged.in");
        }

        final long authorizerId = currentUser.getId();

        Optional<User> u = us.findByEmail(inviteDto.getEmail());
        Optional<Community> c = cs.findById(communityId);
        if(!u.isPresent())
            return GenericResponses.badRequest("user.not.found" , "User email does not exist");
        if(!c.isPresent())
            return GenericResponses.badRequest("community.not.found" , "Community does not exist");
        if (!canAuthorize(communityId, authorizerId)) {
            return GenericResponses.notAModerator();
        }
        if(cs.canAccess(u.get() , c.get()))
            return GenericResponses.conflict("user.has.access" , "cannot invite user");

        boolean success = cs.invite(u.get().getId(), communityId, authorizerId);
        if(success)
            return GenericResponses.success();
        return GenericResponses.conflict("cannot.invite.user" , "cannot invite user");
    }

    @PUT
    @Path("/{communityId}/user/{userId}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response access(@Valid AccessDto accessDto , @PathParam("userId") final long userId, @PathParam("communityId") final long communityId){
        String accessTypeParam = accessDto.getAccessType();
        final User currentUser = commons.currentUser();
        if(currentUser == null){
            return GenericResponses.notAuthorized("not.logged.in");
        }
        System.out.println("current user:" + currentUser.getId());
        final long authorizerId = currentUser.getId();
        LOGGER.info("User {} tried to access community {} with target user {} and desired access type {}" , currentUser.getId(), communityId, userId, accessTypeParam);

        boolean success = false;
        LOGGER.debug("canInteract = {}, canAuthorize = {}", canInteract(userId, authorizerId), canAuthorize(communityId, authorizerId));
        String code = "unknown.error";
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
                        return GenericResponses.cantAccess("user.differs.from.logged.in" , "The authenticated user must be the same as the target one for this action");
                    }
                    success = cs.unblockCommunity(userId, communityId);
                    code = "community.not.blocked";
                }
                else if(currentAccess.isPresent() && currentAccess.get() == AccessType.BANNED){
                    if(!canAuthorize(communityId, authorizerId)){
                        return GenericResponses.cantAccess("not.a.moderator" , "The authenticated user must the community moderator ");
                    }
                    success = cs.liftBan(userId, communityId, authorizerId);
                    code = "user.not.banned";
                }
            }
            return success? GenericResponses.success() : GenericResponses.badRequest(code , null);
        }

        LOGGER.debug("Entrando al switch con access {}", desiredAccessType);

        switch (desiredAccessType) {
            case ADMITTED: {
                if (canAuthorize(communityId, authorizerId)) {
                    success = cs.admitAccess(userId, communityId, authorizerId);
                    code = "cannot.authorize.access";
                } else if (canInteract(userId, authorizerId)) {
                    code = "cannot.accept.invite";
                    success = cs.acceptInvite(userId, authorizerId);
                } else {
                    return GenericResponses.notAuthorized();
                }
                break;
            }
            case KICKED: {
                if (!canAuthorize(communityId, authorizerId)) {
                    return GenericResponses.notAModerator();
                }
                success = cs.kick(userId, communityId, authorizerId);
                code = "cannot.kick.user";
                break;
            }
            case BANNED: {
                if (!canAuthorize(communityId, authorizerId)) {
                    return GenericResponses.notAModerator();
                }
                success = cs.ban(userId, communityId, authorizerId);
                code = "cannot.ban.user";
                break;
            }
            case REQUEST_REJECTED: {
                if (!canAuthorize(communityId, authorizerId)) {
                    return GenericResponses.notAuthorized();
                }
                success = cs.rejectAccess(userId, communityId, authorizerId);
                code = "cannot.reject.request";
                break;
            }
            case INVITED: {
                if (!canAuthorize(communityId, authorizerId)) {
                    return GenericResponses.notAModerator();
                }
                code = "cannot.invite.user";
                success = cs.invite(userId, communityId, authorizerId);
                break;
            }
            case REQUESTED: {
                if (!canInteract(userId, authorizerId)) {
                    return GenericResponses.notAuthorized();
                }
                success = cs.requestAccess(userId, communityId);
                code = "cannot.request.access";
                break;
            }
            case INVITE_REJECTED: {
                if (!canInteract(userId, authorizerId)) {
                    return GenericResponses.notAuthorized();
                }
                success = cs.refuseInvite(userId, communityId);
                code = "user.not.invited";
                break;
            }
            case LEFT: {
                if (!canInteract(userId, authorizerId)) {
                    return GenericResponses.notAuthorized();
                }
                success = cs.leaveCommunity(userId, communityId);
                code = "user.not.a.member";
                break;
            }
            case BLOCKED_COMMUNITY: {
                if (!canInteract(userId, authorizerId)) {
                    return GenericResponses.notAuthorized();
                }
                success = cs.blockCommunity(userId, communityId);
                code = "cannot.block.community";
                break;
            }
        }

        return success? GenericResponses.success() : GenericResponses.badRequest(code , "Error while performing an access action");

    }



    private boolean canAuthorize(long communityId, long authorizerId){
        Optional<Community> maybeCommunity = cs.findById(communityId);
        if(maybeCommunity.isPresent())
            System.out.println("found community:" + maybeCommunity.get().getId() + " with moderator: " + maybeCommunity.get().getModerator().getId());
        System.out.println(" Athorizor:" + authorizerId);
        // Si el autorizador no es el moderador, no tiene acceso a la acción
        return maybeCommunity.isPresent() && authorizerId == maybeCommunity.get().getModerator().getId();
    }
    private boolean canInteract(long userId, long authorizerId){
        return  authorizerId == userId;
    }


}



