package ar.edu.itba.paw.webapp.controller.dto;

import ar.edu.itba.paw.models.Answer;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Date;

public class AnswerDto {
    private Long        id; //TODO esta bien poner id?
    private String      body;
    private URI         owner;
    private URI         question;
    private URI         community;
    private Boolean     verify;
    private Boolean     myVote;
    private Date        time;
    private String      url;
    private Integer     votes;

    public static AnswerDto answerToAnswerDto(Answer a, UriInfo uri){
        AnswerDto answerDto = new AnswerDto();
        answerDto.id = a.getId();
        answerDto.body = a.getBody();
        answerDto.myVote = a.getMyVote();
        answerDto.time = a.getTime();
        answerDto.question = uri.getBaseUriBuilder().path("/questions/").path(String.valueOf(a.getQuestion().getId())).build();
        answerDto.community = uri.getBaseUriBuilder().path("/communities/").path(String.valueOf(a.getQuestion().getForum().getCommunity().getId())).build();
        answerDto.owner = (uri.getBaseUriBuilder().path("/users/").path(String.valueOf(a.getOwner().getId())).build());
        answerDto.verify = a.getVerify();
        answerDto.votes = a.getVotes();
        answerDto.url = uri.getBaseUriBuilder().path("/answers/").path(String.valueOf(a.getId())).build().toString();
        return answerDto;
    }

    public String getUrl() {
        return url;
    }

    public String getBody() {
        return body;
    }

    public Boolean getMyVote() {
        return myVote;
    }

    public Date getTime() {
        return time;
    }

    public Boolean getVerify() {
        return verify;
    }

    public void setOwner(URI owner) {
        this.owner = owner;
    }

    public void setCommunity(URI community) {
        this.community = community;
    }

    public void setQuestion(URI question) {
        this.question = question;
    }

    public URI getOwner() {
        return owner;
    }

    public URI getQuestion() {
        return question;
    }

    public URI getCommunity() {
        return community;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setBody(String body) {
        this.body = body;
    }


    public void setMyVote(Boolean myVote) {
        this.myVote = myVote;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void setVerify(Boolean verify) {
        this.verify = verify;
    }

    public void setVotes(Integer votes) {
        this.votes = votes;
    }

    public Integer getVotes() {
        return votes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
