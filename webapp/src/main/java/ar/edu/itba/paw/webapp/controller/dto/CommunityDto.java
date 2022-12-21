package ar.edu.itba.paw.webapp.controller.dto;

import ar.edu.itba.paw.models.Community;

import javax.ws.rs.core.UriInfo;

public class CommunityDto {

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private Long id;
    private String name;
    private String description;
    private UserDto moderator;
    private Long userCount;
    private Long notifications;
    private String url;

    public static CommunityDto communityToCommunityDto(Community c, UriInfo uri){
        CommunityDto communityDto = new CommunityDto();
        communityDto.notifications = c.getNotifications();
        communityDto.name = c.getName();
        communityDto.description = c.getDescription();
        communityDto.url = uri.getBaseUriBuilder().path("/communities/").path(String.valueOf(c.getId())).build().toString();
        communityDto.id = c.getId();
        return communityDto;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public Long getNotifications() {
        return notifications;
    }

    public Long getUserCount() {
        return userCount;
    }

    public UserDto getModerator() {
        return moderator;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setModerator(UserDto moderator) {
        this.moderator = moderator;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNotifications(Long notifications) {
        this.notifications = notifications;
    }

    public void setUserCount(Long userCount) {
        this.userCount = userCount;
    }


    @Override
    public String toString() {
        return "CommunityDto{" +
                "name='" + name + '\'' +
                ", moderator=" + moderator +
                ", userCount=" + userCount +
                ", notifications=" + notifications +
                ", url='" + url + '\'' +
                '}';
    }
}
