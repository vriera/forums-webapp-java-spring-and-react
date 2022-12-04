package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Notification;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.controller.utils.GenericResponses;
import ar.edu.itba.paw.webapp.controller.dto.NotificationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.util.Optional;

@Component
@Path("notifications")
public class NotificationsController {

    @Autowired
    private UserService us;

    @Context
    private UriInfo uriInfo;

    @Autowired
    private Commons commons;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @GET
    @Path("/")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response getQuestion() {
        User u = commons.currentUser();
        if( u == null ){
            return GenericResponses.notAuthorized();
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
}
