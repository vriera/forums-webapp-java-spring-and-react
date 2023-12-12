package ar.edu.itba.paw.webapp.dto.output;

import ar.edu.itba.paw.models.CommunityNotifications;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class CommunityNotificationsDto {


    private URI community;



    private Long notifications;



    private String url;
    public CommunityNotificationsDto(){

    }

    public static CommunityNotificationsDto toNotificationDto(CommunityNotifications cn , UriInfo uriInfo){
        CommunityNotificationsDto cndto = new CommunityNotificationsDto();
        String communityId = String.valueOf(cn.getCommunity().getId());
        cndto.setCommunity(uriInfo.getBaseUriBuilder().path("/communities").path(communityId).build());
        cndto.setUrl(uriInfo.getBaseUriBuilder().path("/communities").path(communityId).path("/notifications").build().toString());
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
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
