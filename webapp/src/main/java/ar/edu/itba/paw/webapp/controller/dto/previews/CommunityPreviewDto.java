package ar.edu.itba.paw.webapp.controller.dto.previews;

import ar.edu.itba.paw.models.Community;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class CommunityPreviewDto {

    private Long id;
    private String name;

    public URI getModerator() {
        return moderator;
    }

    public void setModerator(URI moderator) {
        this.moderator = moderator;
    }

    private URI moderator;
    private String description;
    private Long userCount;
    private URI uri;


    public CommunityPreviewDto(){}

    public static CommunityPreviewDto toCommunityPreview(Community c , UriInfo uri){
        CommunityPreviewDto cp = new CommunityPreviewDto();
        cp.setName(c.getName());
        cp.setModerator(uri.getBaseUriBuilder().path("/user/").path(String.valueOf(c.getModerator().getId())).build());
        cp.setUri(uri.getBaseUriBuilder().path("/community/").path(String.valueOf(c.getId())).build());
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
