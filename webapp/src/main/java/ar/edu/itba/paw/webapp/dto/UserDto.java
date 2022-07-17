package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.User;

import javax.persistence.Column;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.net.URI;


public class UserDto {
    //id no tiene sentido

    private Long id;


    private String password;

    private String username;

    private String email;

    private String url;

    private URI karma;

    public static UserDto userToUserDto(User u,  UriInfo uri){
        UserDto userDto = new UserDto();
        userDto.username = u.getUsername();
        userDto.email = u.getEmail();
        userDto.id = u.getId();
        userDto.karma = uri.getBaseUriBuilder().path("/karma/").path(String.valueOf(u.getId())).build();
        userDto.url = uri.getBaseUriBuilder().path("/users/").path(String.valueOf(u.getId())).build().toString();
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}