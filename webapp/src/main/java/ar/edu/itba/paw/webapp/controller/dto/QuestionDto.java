package ar.edu.itba.paw.webapp.controller.dto;

import ar.edu.itba.paw.models.*;
import org.glassfish.jersey.server.Uri;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class QuestionDto {

    private String title;

    private String body;

    public URI getOwner() {
        return owner;
    }

    public void setOwner(URI owner) {
        this.owner = owner;
    }

    private URI owner;

    private URI forum;

    private int votes;

    private Boolean myVote;

    private URI image;

    private SmartDate smartDate;

    private String url;

    private URI community;

    public static QuestionDto questionDtoToQuestionDto(Question q, UriInfo uri){
        QuestionDto questionDto = new QuestionDto();
        questionDto.body = q.getBody();
        questionDto.smartDate = q.getSmartDate();
        questionDto.myVote = q.getMyVote();
        questionDto.owner = uri.getBaseUriBuilder().path("/user/").path(String.valueOf(q.getOwner().getId())).build();
        questionDto.votes = q.getVotes();
        questionDto.title = q.getTitle();
        questionDto.forum = uri.getBaseUriBuilder().path("/forum/").path(String.valueOf(q.getForum().getId())).build();
        if(q.getImageId()!=null){
            questionDto.image = uri.getBaseUriBuilder().path("/image/").path(String.valueOf(q.getImageId())).build();
        }

        if(q.getCommunity()!=null){
            questionDto.community = uri.getBaseUriBuilder().path("/community/").path(String.valueOf(q.getForum().getCommunity().getId())).build();
        }

        questionDto.url = uri.getBaseUriBuilder().path("/question/").path(String.valueOf(q.getId())).build().toString();
        return questionDto;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setMyVote(Boolean myVote) {
        this.myVote = myVote;
    }


    public void setVotes(int votes) {
        this.votes = votes;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setForum(URI forum) {
        this.forum = forum;
    }

    public void setImage(URI image) {
        this.image = image;
    }

    public void setSmartDate(SmartDate smartDate) {
        this.smartDate = smartDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }


    public Boolean getMyVote() {
        return myVote;
    }


    public String getBody() {
        return body;
    }

    public String getTitle() {
        return title;
    }

    public int getVotes() {
        return votes;
    }


    public URI getForum() {
        return forum;
    }

    public SmartDate getSmartDate() {
        return smartDate;
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
}
