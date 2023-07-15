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
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response getNotification(@PathParam("userId") int  userId) {
        User u = commons.currentUser();
        final Optional<Notification> notifications = us.getNotifications(u.getId());
        if(!notifications.isPresent()){
            return Response.noContent().build();
        }
        NotificationDto notificationsDto = NotificationDto.notificationToNotificationDto(notifications.get() , uriInfo);
        return Response.ok(new GenericEntity<NotificationDto>(notificationsDto) {}).build();
    }

    @GET
    @Path("/communities/{communityId}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response getNotificationOnCommunity(@PathParam("communityId") int communityId) {
        Optional<Community> c= cs.findById(communityId);
        if(!c.isPresent())
            return GenericResponses.notFound();

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
