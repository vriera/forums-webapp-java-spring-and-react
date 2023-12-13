package ar.edu.itba.paw.webapp.dto.output;

import ar.edu.itba.paw.models.Answer;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Date;

public class AnswerDto {
    private Long id; // TODO esta bien poner id?
    private String body;
    private URI owner;
    private URI question;
    private URI community;
    private URI votes;

    private Integer voteCount;
    private Boolean verified;
    private Date time;
    private String url;

    public static AnswerDto answerToAnswerDto(Answer a, UriInfo uri) {
        AnswerDto answerDto = new AnswerDto();
        answerDto.id = a.getId();
        answerDto.body = a.getBody();
        answerDto.time = a.getTime();
        answerDto.question = uri.getBaseUriBuilder().path("/questions/").path(String.valueOf(a.getQuestion().getId()))
                .build();
        answerDto.community = uri.getBaseUriBuilder().path("/communities/")
                .path(String.valueOf(a.getQuestion().getForum().getCommunity().getId())).build();
        answerDto.owner = (uri.getBaseUriBuilder().path("/users/").path(String.valueOf(a.getOwner().getId())).build());
        answerDto.verified = a.getVerify();
        answerDto.voteCount = a.getVotes();
        answerDto.votes = uri.getBaseUriBuilder().path("/answers/").path(String.valueOf(a.getId())).path("/votes")
                .build();
        answerDto.url = uri.getBaseUriBuilder().path("/answers/").path(String.valueOf(a.getId())).build().toString();
        return answerDto;
    }

    public String getUrl() {
        return url;
    }

    public String getBody() {
        return body;
    }

    public Date getTime() {
        return time;
    }

    public Boolean getVerify() {
        return verified;
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

    public void setTime(Date time) {
        this.time = time;
    }

    public void setVerify(Boolean verify) {
        this.verified = verify;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public URI getVotes() {
        return votes;
    }

    public void setVotes(URI votes) {
        this.votes = votes;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }
}
