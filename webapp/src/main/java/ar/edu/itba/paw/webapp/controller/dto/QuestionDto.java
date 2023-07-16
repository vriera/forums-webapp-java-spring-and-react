package ar.edu.itba.paw.webapp.controller.dto;

import ar.edu.itba.paw.models.*;
import org.glassfish.jersey.server.Uri;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Date;

public class QuestionDto {

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    private long id;
    private String title;

    private String body;

    private URI owner;

    private URI votes;

    public URI getVotes() {
        return votes;
    }

    public void setVotes(URI votes) {
        this.votes = votes;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    private int voteCount;

    private URI image;

    private Date time;

    private String url;

    private URI community;


    public static QuestionDto questionToQuestionDto(Question q, UriInfo uri){
        QuestionDto questionDto = new QuestionDto();
        questionDto.body = q.getBody();
        questionDto.time = q.getTime();

        questionDto.owner = uri.getBaseUriBuilder().path("/users/").path(String.valueOf(q.getOwner().getId())).build();
        questionDto.votes = uri.getBaseUriBuilder().path("/questions/").path(String.valueOf(q.getId())).path("/votes").build();
        questionDto.voteCount = q.getVotes();
        questionDto.title = q.getTitle();

        if(q.getImageId()!=null){
            questionDto.image = uri.getBaseUriBuilder().path("/images/").path(String.valueOf(q.getImageId())).build();
        }

        if(q.getCommunity()!=null){
            questionDto.community = uri.getBaseUriBuilder().path("/communities/").path(String.valueOf(q.getForum().getCommunity().getId())).build();
        }
        questionDto.id = q.getId();
        questionDto.url = uri.getBaseUriBuilder().path("/questions/").path(String.valueOf(q.getId())).build().toString();
        return questionDto;
    }

    public void setUrl(String url) {
        this.url = url;
    }




    public void setBody(String body) {
        this.body = body;
    }



    public void setImage(URI image) {
        this.image = image;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public String getBody() {
        return body;
    }

    public String getTitle() {
        return title;
    }





    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public URI getImage() {
        return image;
    }

    public URI getCommunity() {
        return community;
    }

    public void setCommunity(URI community) {
        this.community = community;
    }

    public URI getOwner() {
        return owner;
    }

    public void setOwner(URI owner) {
        this.owner = owner;
    }
}
