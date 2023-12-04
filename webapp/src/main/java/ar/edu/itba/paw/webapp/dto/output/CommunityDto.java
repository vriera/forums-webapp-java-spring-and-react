package ar.edu.itba.paw.webapp.dto.output;

import ar.edu.itba.paw.models.Community;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class CommunityDto {

    private Long id;
    private String name;
    private String description;
    private URI admittedUsers;


    private URI questions;
    private Long userCount;
    private Long notifications;
    private String url;

    public static CommunityDto communityToCommunityDto(Community c, UriInfo uri ){
        CommunityDto communityDto = new CommunityDto();
        communityDto.notifications = c.getNotifications();
        communityDto.name = c.getName();
        communityDto.description = c.getDescription();
        communityDto.url = uri.getBaseUriBuilder().path("/communities/").path(String.valueOf(c.getId())).build().toString();
        communityDto.id = c.getId();
        communityDto.moderator = uri.getBaseUriBuilder().path("/users/").path(String.valueOf(c.getModerator().getId())).build();
        communityDto.userCount = c.getUserCount();
        communityDto.questions = uri.getBaseUriBuilder().path("/questions").queryParam("communityId" , c.getId()).build();
        if(c.getModerator().getId() != 0)
            communityDto.admittedUsers = uri.getBaseUriBuilder().path("/users/").path("/admitted").queryParam("moderatorId" , c.getModerator().getId() ).queryParam("communityId" , c.getId()).build();
        return communityDto;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public URI getModerator() {
        return moderator;
    }

    public void setModerator(URI moderator) {
        this.moderator = moderator;
    }

    private URI moderator; //TODO PASAR A URI Y SI QUIEREN PONER METADATA





    public URI getAdmittedUsers() {
        return admittedUsers;
    }

    public void setAdmittedUsers(URI admittedUsers) {
        this.admittedUsers = admittedUsers;
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


    public URI getQuestions() {
        return questions;
    }

    public void setQuestions(URI questions) {
        this.questions = questions;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUrl(String url) {
        this.url = url;
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
