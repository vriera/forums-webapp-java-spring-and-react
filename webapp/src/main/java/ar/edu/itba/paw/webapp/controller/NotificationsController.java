package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.interfaces.services.CommunityService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.CommunityNotifications;
import ar.edu.itba.paw.models.Notification;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.controller.dto.CommunityNotificationsDto;
import ar.edu.itba.paw.webapp.controller.utils.GenericResponses;
import ar.edu.itba.paw.webapp.controller.dto.NotificationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Optional;

@Component
@Path("notifications")
public class NotificationsController {

    @Autowired
    private UserService us;

    @Context
    private UriInfo uriInfo;

    @Context
    private CommunityService cs;
    @Autowired
    private Commons commons;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @GET
    @Path("/{userId}")
    @Produces(value = { MediaType.APPLICATION_JSON, }) //TODO: pasar esto a SPRING SECURITY
    public Response getNotification(@PathParam("userId") int  userId) {
        User u = commons.currentUser();
        if( u == null ){
            return GenericResponses.notAuthorized();
        }
        if(u.getId() != userId){
            return GenericResponses.cantAccess();
        }
        final Optional<Notification> notifications = us.getNotifications(u.getId());
        if(!notifications.isPresent()){
            return Response.noContent().build();
        }
        NotificationDto nDto = NotificationDto.notificationToNotificationDto(notifications.get() , uriInfo);
        return Response.ok(new GenericEntity<NotificationDto>(nDto) {
        })
                .build();
    }

    @GET
    @Path("/communities/{communityId}")
    @Produces(value = { MediaType.APPLICATION_JSON, }) //TODO: pasar esto a SPRING SECURITY
    public Response getNotificationOnCommunity(@PathParam("communityId") int communityId) {
        User u = commons.currentUser();
        if( u == null ){
            return GenericResponses.notAuthorized();
        }
        Optional<Community> c= cs.findById(communityId);

        if(!c.isPresent())
            return GenericResponses.notFound();

        if(c.get().getModerator().getId() != u.getId()){
            return GenericResponses.cantAccess("not.a.moderator" , "The authenticated user must be a community moderator");
        }
        Optional<CommunityNotifications> notifications = cs.getCommunityNotificationsById(communityId);
        if(!notifications.isPresent()){
            return Response.noContent().build();
        }
        CommunityNotificationsDto cnDto = CommunityNotificationsDto.toNotificationDtio(notifications.get() , uriInfo);
        return Response.ok(
                new GenericEntity<CommunityNotificationsDto>(cnDto){}
        ).build();
    }
}
