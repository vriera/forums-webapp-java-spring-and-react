package ar.edu.itba.paw.webapp.controller.dto;

import ar.edu.itba.paw.models.CommunityNotifications;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class CommunityNotificationsDto {


    private URI community;



    private Long notifications;
    public CommunityNotificationsDto(){

    }

    public static CommunityNotificationsDto toNotificationDtio(CommunityNotifications cn , UriInfo uriInfo){
        CommunityNotificationsDto cndto = new CommunityNotificationsDto();
        cndto.setCommunity(uriInfo.getBaseUriBuilder().path("/communities/").path(String.valueOf(cn.getCommunity().getId())).build());
        cndto.setNotifications(cn.getNotifications());
        return cndto;
    }


    public URI getCommunity() {
        return community;
    }

    public void setCommunity(URI community) {
        this.community = community;
    }

    public Long getNotifications() {
        return notifications;
    }

    public void setNotifications(Long notifications) {
        this.notifications = notifications;
    }

}
