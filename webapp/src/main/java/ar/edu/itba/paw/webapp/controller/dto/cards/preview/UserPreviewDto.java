package ar.edu.itba.paw.webapp.controller.dto.cards.preview;

import ar.edu.itba.paw.models.User;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class UserPreviewDto {


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    //        private Long id;
    private String username;
    private URI uri;


    public UserPreviewDto(){}

    public static UserPreviewDto toUserPreview(User u , UriInfo uri){
        UserPreviewDto up = new UserPreviewDto();
        up.setUsername(u.getUsername());
        up.setUri(uri.getBaseUriBuilder().path("/users/").path(String.valueOf(u.getId())).build());
        return  up;
    }

}
