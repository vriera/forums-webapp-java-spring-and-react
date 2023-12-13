package ar.edu.itba.paw.webapp.dto.output;

import ar.edu.itba.paw.models.User;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class UserDto {
    // id no tiene sentido

    private Long id;

    private String username;

    private String email;

    private String url;

    private URI karma;

    public URI getModeratedCommunities() {
        return moderatedCommunities;
    }

    public void setModeratedCommunities(URI moderatedCommunities) {
        this.moderatedCommunities = moderatedCommunities;
    }

    private URI moderatedCommunities;

    public static UserDto userToUserDto(User u, UriInfo uri) {
        UserDto userDto = new UserDto();
        userDto.username = u.getUsername();
        userDto.email = u.getEmail();
        userDto.id = u.getId();
        String userId = String.valueOf(u.getId());
        userDto.karma = uri.getBaseUriBuilder().path("/users/").path(userId).path("/karma").build();
        userDto.url = uri.getBaseUriBuilder().path("/users/").path(userId).build().toString();
        userDto.moderatedCommunities = uri.getBaseUriBuilder().path("/communities").queryParam("moderatorId", userId)
                .build();
        return userDto;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public URI getKarma() {
        return karma;
    }

    public void setKarma(URI karma) {
        this.karma = karma;
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", url='" + url + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}