package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.interfaces.services.CommunityService;
import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.auth.PawUserDetailsService;
import ar.edu.itba.paw.webapp.controller.dto.*;
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
import java.util.ArrayList;
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
    @Path("/")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response list(@DefaultValue("1") @QueryParam("page") int page,
                         @DefaultValue("5") @QueryParam("limit") final Integer limit,
                         @DefaultValue("") @QueryParam("query") String query) {
        List<Community> cl = ss.searchCommunity(query ,page, limit);
        if(cl.isEmpty())  return Response.noContent().build();
        int total = (int) Math.ceil(ss.searchCommunityCount(query) / (double) limit);
        UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
        if(!query.equals(""))
            uriBuilder.queryParam("query" , query);
        return communityListToResponse(cl , page , total , uriBuilder);
    }

    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getCommunity(@PathParam("id") int id ) {
        // FIXME: Lógica de negocios!
        if(id<0) return GenericResponses.badRequest("illegal.id" , "Id cannot be negative");
        Optional<Community> c = cs.findById(id);

        if(!c.isPresent()) return GenericResponses.notFound();

        Community community = c.get();
        community.setUserCount(0L);
        Optional<Number> uc = cs.getUserCount(id);
        if(uc.isPresent()) community.setUserCount(uc.get().longValue());

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


        //Name
        //Description
        //TODO: the validations --> VAN EN LOS SERVICIOS NO ACA
        if(cs.findByName(communityForm.getName()).isPresent())
            return GenericResponses.conflict("community.name.taken" , "A community with the given name already exists");


        final String title = communityForm.getName();
        final String description = communityForm.getDescription();
        Optional<Community> c = cs.create(title, description, u);

        if (!c.isPresent()) {
            return GenericResponses.serverError(); //TODO: ESTA BIEN QUE SEA SERVER Error?
        }

        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(c.get().getId())).build();
        return Response.created(uri).build();
    }

    //TODO: sacar la logica de negocios y dejarla en el services
    @PUT
    @Path("/{communityId}/invite")
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response access(@Valid InviteDto inviteDto, @PathParam("communityId") final long communityId) {
//        String accessTypeParam = accessDto.getAccessType();
        final User currentUser = commons.currentUser();

        final long authorizerId = currentUser.getId();

        Optional<User> u = us.findByEmail(inviteDto.getEmail());
        Optional<Community> c = cs.findById(communityId);

        //TODO: sacar la logica de negocios y dejarla en el services desde aca
        if(!u.isPresent())
            return GenericResponses.badRequest("user.not.found" , "User email does not exist");
        if(!c.isPresent())
            return GenericResponses.badRequest("community.not.found" , "Community does not exist");
        if (!canAuthorize(communityId, authorizerId)) {
            return GenericResponses.notAModerator();
        }

        if(cs.canAccess(u.get() , c.get()))
            return GenericResponses.conflict("user.has.access" , "cannot invite user");
        //TODO: sacar la logica de negocios y dejarla en el services hasta aca

        boolean success = cs.invite(u.get().getId(), communityId, authorizerId);
        if(success)
            return GenericResponses.success();
        return GenericResponses.conflict("cannot.invite.user" , "cannot invite user");


    }

    @GET
    @Path("/{communityId}/user/{userId}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response canAccess(@PathParam("userId") final long userId, @PathParam("communityId") final long communityId){
        Optional<Community> c = cs.findById(communityId);
        if(!c.isPresent()) return GenericResponses.notFound();

        if(c.get().getModerator().getId() == 0){
            AccessInfoDto accessInfoDto = new AccessInfoDto();
            accessInfoDto.setCanAccess(true);
            accessInfoDto.setUri(uriInfo.getBaseUriBuilder().path("/communities/").path(String.valueOf(communityId)).path("/users/").path(String.valueOf(userId)).build());
            return Response.ok( new GenericEntity<AccessInfoDto>(accessInfoDto){}).build();
        }

        //TODO: SACAR TODA LA LOGICA Y DEJARLA EN LOS SERVICIES
        Optional<User> u = us.findById(userId);
        Boolean access = cs.canAccess(u.get() , c.get());
        Optional<AccessType> accessType =cs.getAccess(userId , communityId);
        //TODO: AGREGAR LOS URIS DE COMMUNITY Y USERID QUEDARIA BONITO
        AccessInfoDto accessInfoDto = new AccessInfoDto();
        accessInfoDto.setCanAccess(access);
        accessInfoDto.setUri(uriInfo.getBaseUriBuilder().path("/communities/").path(String.valueOf(communityId)).path("/users/").path(String.valueOf(userId)).build());
        //TODO: SACAR TODA LA LOGICA Y DEJARLA EN LOS SERVICIES
        if(accessType.isPresent()){
            accessInfoDto.setAccessType(accessType.get().ordinal());
        }

        return Response.ok( new GenericEntity<AccessInfoDto>(accessInfoDto){}).build();
    }
    @PUT
    @Path("/{communityId}/user/{userId}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response access(@Valid AccessDto accessDto , @PathParam("userId") final long userId, @PathParam("communityId") final long communityId){
        String accessTypeParam = accessDto.getAccessType();
        final User currentUser = commons.currentUser();
        final long authorizerId = currentUser.getId();
        LOGGER.info("User {} tried to access community {} with target user {} and desired access type {}" , currentUser.getId(), communityId, userId, accessTypeParam);
        //TODO: SACAR TODA LA LOGICA Y DEJARLA EN LOS SERVICIES
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
            //TODO: SACAR TODA LA LOGICA Y DEJARLA EN LOS SERVICIES
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
            LOGGER.info("found community:" + maybeCommunity.get().getId() + " with moderator: " + maybeCommunity.get().getModerator().getId());
        LOGGER.info(" Athorizor:" + authorizerId);
        // Si el autorizador no es el moderador, no tiene acceso a la acción
        return maybeCommunity.isPresent() && authorizerId == maybeCommunity.get().getModerator().getId();
    }
    private boolean canInteract(long userId, long authorizerId){
        return  authorizerId == userId;
    }




    @GET
    @Path("/askable") //TODO: pasar esto a SPRING SECURITY
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response list(@DefaultValue("1") @QueryParam("page") int page,@DefaultValue("-1") @QueryParam("userId") int userId) {


        int size = PAGE_SIZE;
        int offset = (page - 1) * size;
        //TODO FALTA not found
        if(userId < 0){
            List<Community> cl = cs.getPublicCommunities();
            UriBuilder uri = uriInfo.getAbsolutePathBuilder();
            if(userId != -1 )
                uri.queryParam("userId" , userId);
            if(page != 1)
                cl = new ArrayList<>();
            return communityListToResponse(cl , 1 , 1 , uri );
        }

        User u = commons.currentUser();

        if( u == null)
            return GenericResponses.notAuthorized();
        if( userId != -1 && u.getId() != userId)
            return GenericResponses.cantAccess();

        List<Community> cl = cs.list(u.getId() , size , offset);

        int total = (int) Math.ceil(cs.listCount(u.getId()) / (double)size);
        UriBuilder uri = uriInfo.getAbsolutePathBuilder();
        if(userId != -1 )
            uri.queryParam("userId" , userId);
        return communityListToResponse(cl , page , total , uri );
    }

    @GET
    @Path("/moderated")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response getModeratedCommunities(@QueryParam("userId") @DefaultValue("-1") final long id ,@QueryParam("page") @DefaultValue("1") int page) {

        final User user = us.findById(id).orElse(null);

        if (user != null) {
            List<Community> communities = us.getModeratedCommunities( id , page -1 );
            if(communities.isEmpty())  return Response.noContent().build();
            //communities = communities.stream().map(x ->addUserCount(x) ).collect(Collectors.toList());
            int pages = (int) us.getModeratedCommunitiesPages(id);
            UriBuilder uri = uriInfo.getAbsolutePathBuilder();
            if(id != -1 )
                uri.queryParam("userdId" , id );

            return communityListToResponse(communities , page , pages , uri);


        } else {
            return GenericResponses.notFound();
        }
    }


    @GET
    @Path("/admitted")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response getAdmittedCommunities(@QueryParam("page") @DefaultValue("1") int page , @QueryParam("userId") @DefaultValue("-1") int userId) {
        return  getInvitedByAccessLevel(page , userId , AccessType.ADMITTED);
    }
    @GET
    @Path("/requested")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response getRequestedCommunities(@QueryParam("page") @DefaultValue("1") int page , @QueryParam("userId") @DefaultValue("-1") int userId) {
        return  getInvitedByAccessLevel(page , userId , AccessType.REQUESTED);
    }

    @GET
    @Path("/request-rejected")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response getRequestRejected(@QueryParam("page") @DefaultValue("1") int page , @QueryParam("userId") @DefaultValue("-1") int userId) {
        return  getInvitedByAccessLevel(page , userId , AccessType.REQUEST_REJECTED);
    }
    @GET
    @Path("/invited")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response getInvitedCommunities(@QueryParam("page") @DefaultValue("1") int page , @QueryParam("userId") @DefaultValue("-1") int userId) {
        return  getInvitedByAccessLevel(page , userId , AccessType.INVITED);
    }
    @GET
    @Path("/invite-rejected")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response getInviteRejectedCommunities(@QueryParam("page") @DefaultValue("1") int page , @QueryParam("userId") @DefaultValue("-1") int userId) {
        return  getInvitedByAccessLevel(page , userId , AccessType.INVITE_REJECTED);
    }
    @GET
    @Path("/left")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response getLeftCommunities(@QueryParam("page") @DefaultValue("1") int page , @QueryParam("userId") @DefaultValue("-1") int userId) {
        return  getInvitedByAccessLevel(page , userId , AccessType.LEFT);
    }

    @GET
    @Path("/blocked")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response getBlockedCommunities(@QueryParam("page") @DefaultValue("1") int page ,@QueryParam("userId") @DefaultValue("1") int userId ) {
        return  getInvitedByAccessLevel(page , userId , AccessType.BLOCKED_COMMUNITY);
    }

    @GET
    @Path("/kicked")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response getKickedCommunities(@QueryParam("page") @DefaultValue("1") int page ,@QueryParam("userId") @DefaultValue("1") int userId ) {
        return  getInvitedByAccessLevel(page , userId , AccessType.KICKED);
    }


    @GET
    @Path("/banned") //TODO: pasar esto a SPRING SECURITY
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response getBanned(@QueryParam("page") @DefaultValue("1") int page ,@QueryParam("userId") @DefaultValue("1") int userId ) {
        return  getInvitedByAccessLevel(page , userId , AccessType.BANNED);
    }



    private Response getInvitedByAccessLevel(int page , int userId , AccessType accessType){

        final User u = commons.currentUser();

        if(page < 1 )
            return GenericResponses.badRequest();

        int pageSize = 5;
        int pages = (int) us.getCommunitiesByAccessTypePages(userId,  accessType);
        List<Community> communities = us.getCommunitiesByAccessType(userId, accessType, page -1 );
        if(communities.isEmpty())  return Response.noContent().build();
        UriBuilder uri = uriInfo.getBaseUriBuilder();
        if( userId != -1)
            uri.queryParam("userId" , userId);

        return communityListToResponse(communities , page , pages , uri);

    }



    private Response communityListToResponse(List<Community> cl , int page , int pages , UriBuilder uri){

        if(cl.isEmpty())  return Response.noContent().build();
        List<CommunityDto> cldto = cl.stream().map(x-> CommunityDto.communityToCommunityDto(x,uriInfo)).collect(Collectors.toList());
        Response.ResponseBuilder res =  Response.ok(
                new GenericEntity<List<CommunityDto>>(cldto) {
                }
        );
        return PaginationHeaderUtils.addPaginationLinks(page , pages , uri , res);

    }






}



