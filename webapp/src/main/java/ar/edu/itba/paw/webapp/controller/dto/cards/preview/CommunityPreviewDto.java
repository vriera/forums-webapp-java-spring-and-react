package ar.edu.itba.paw.webapp.controller.dto.cards.preview;

import ar.edu.itba.paw.models.Community;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class CommunityPreviewDto {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    //        private Long id;
        private String name;
        private URI uri;


        public CommunityPreviewDto(){}

        public static CommunityPreviewDto toCommunityPreview(Community c , UriInfo uri){
            CommunityPreviewDto cp = new CommunityPreviewDto();
            cp.setName(c.getName());
            cp.setUri(uri.getBaseUriBuilder().path("/community/").path(String.valueOf(c.getId())).build());
            return  cp;
        }


}
