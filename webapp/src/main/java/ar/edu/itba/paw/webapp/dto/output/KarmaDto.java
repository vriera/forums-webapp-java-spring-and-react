package ar.edu.itba.paw.webapp.dto.output;

import ar.edu.itba.paw.models.Karma;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class KarmaDto {

    public Long getKarma() {
        return karma;
    }

    public void setKarma(Long karma) {
        this.karma = karma;
    }

    public URI getUser() {
        return user;
    }

    public void setUser(URI user) {
        this.user = user;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private Long karma;
    private URI user;
    private String url;



    public static KarmaDto KarmaToKarmaDto(Karma k , UriInfo uri){

        KarmaDto uk = new KarmaDto();
        String userId = String.valueOf(k.getUser().getId());
        uk.setUser(uri.getBaseUriBuilder().path("/users/").path(userId).build());
        uk.setKarma(k.getKarma());
        uk.setUrl(uri.getBaseUriBuilder().path("/users/").path(userId).path("/karma").build().toString());
        return uk;
    }

    @Override
    public String toString() {
        return "KarmaDto{" +
                "karma='" + karma + '\'' +
                ", user='" + user + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
