package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Question;

import javax.ws.rs.core.UriInfo;

public class CommunityDto {

    private String name;
    private String description;
    private UserDto moderator;
    private Long userCount;
    private Long notifications;
    private String url;

    public static CommunityDto communityToCommunityDto(Community c, UriInfo uri){
        CommunityDto communityDto = new CommunityDto();
        communityDto.moderator = UserDto.userToUserDto(c.getModerator(),uri);
        communityDto.notifications = c.getNotifications();
        communityDto.name = c.getName();
        communityDto.description = c.getDescription();
        communityDto.url = uri.getBaseUriBuilder().path("/community/").path(String.valueOf(c.getId())).build().toString();
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
