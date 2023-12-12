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


    private URI notifications;
    private String url;

    public static CommunityDto communityToCommunityDto(Community c, UriInfo uri ){
        CommunityDto communityDto = new CommunityDto();
        String communityId = String.valueOf(c.getId());
        String moderatorId = String.valueOf(c.getModerator().getId());
        communityDto.id = c.getId();
        communityDto.url = uri.getBaseUriBuilder().path("/communities/").path(communityId).build().toString();
        communityDto.description = c.getDescription();
        communityDto.moderator = uri.getBaseUriBuilder().path("/users/").path(moderatorId).build();
        communityDto.userCount = c.getUserCount();

//        communityDto.notifications =
        communityDto.name = c.getName();
        communityDto.notifications = uri.getBaseUriBuilder().path("/communities").path(communityId).path("/notifications").build();
        communityDto.questions = uri.getBaseUriBuilder().path("/questions").queryParam("communityId" , c.getId()).build();
        if(c.getModerator().getId() != 0)
            communityDto.admittedUsers = uri.getBaseUriBuilder().path("/users").queryParam("communityId" , c.getId()).build();

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

    private URI moderator;





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


    public void setUserCount(Long userCount) {
        this.userCount = userCount;
    }


    public URI getNotifications() {
        return notifications;
    }

    public void setNotifications(URI notifications) {
        this.notifications = notifications;
    }
    @Override
    public String toString() {
        return "CommunityDto{" +
                "name='" + name + '\'' +
                ", moderator=" + moderator +
                ", userCount=" + userCount +
//                ", notifications=" + notifications +
                ", url='" + url + '\'' +
                '}';
    }
}
