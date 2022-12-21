package ar.edu.itba.paw.webapp.controller.dto;

import ar.edu.itba.paw.models.Notification;

import javax.ws.rs.core.UriInfo;
import java.net.URI;


public class NotificationDto {

    private URI user;

    private Long requests;

    private Long invites;

    private Long total;

    private String url;



    @Override
    public String toString() {
        return "KarmaDto{" +
                "requests='" + requests + '\'' +
                ", invites='" + invites + '\'' +
                ", total='" + total + '\'' +
                ", user='" + user + '\'' +
                ", url='" + url + '\'' +
                '}';
    }



    public static NotificationDto notificationToNotificationDto(Notification n, UriInfo uri){
        NotificationDto nDto = new NotificationDto();
        nDto.setUser(uri.getBaseUriBuilder().path("/users/").path(String.valueOf(n.getUser().getId())).build());
        nDto.setRequests(n.getRequests());
        nDto.setInvites(n.getInvites());
        nDto.setTotal(n.getTotal());
        nDto.setUrl(uri.getBaseUriBuilder().path("/notifications/").path(String.valueOf(n.getUser().getId())).build().toString());
        return nDto;
    }

    public URI getUser() {
        return user;
    }

    public void setUser(URI user) {
        this.user = user;
    }

    public Long getRequests() {
        return requests;
    }

    public void setRequests(Long requests) {
        this.requests = requests;
    }

    public Long getInvites() {
        return invites;
    }

    public void setInvites(Long invites) {
        this.invites = invites;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
