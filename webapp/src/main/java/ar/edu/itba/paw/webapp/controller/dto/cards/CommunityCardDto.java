package ar.edu.itba.paw.webapp.controller.dto.cards;

import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.webapp.controller.dto.cards.preview.UserPreviewDto;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class CommunityCardDto {

    private Long id;
    private String name;


    public UserPreviewDto getModerator() {
        return moderator;
    }

    public void setModerator(UserPreviewDto moderator) {
        this.moderator = moderator;
    }

    private UserPreviewDto moderator;
    private String description;
    private Long userCount;
    private URI uri;


    public CommunityCardDto(){}

    public static CommunityCardDto toCommunityPreview(Community c , UriInfo uri){
        CommunityCardDto cp = new CommunityCardDto();
        cp.setName(c.getName());
        cp.setModerator(UserPreviewDto.toUserPreview(c.getModerator() , uri));
        cp.setUri(uri.getBaseUriBuilder().path("/community-card/").path(String.valueOf(c.getId())).build());
        cp.setId(c.getId());
        cp.setDescription(c.getDescription());
        cp.setUserCount(c.getUserCount());
        return  cp;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getUserCount() {
        return userCount;
    }

    public void setUserCount(Long userCount) {
        this.userCount = userCount;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }
}
