package ar.edu.itba.paw.webapp.controller.dto;

import ar.edu.itba.paw.models.Answer;
import ar.edu.itba.paw.models.Question;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Date;

public class AnswerDto {
    private String      body;
    private URI         owner;
    private Question         question;
    private Boolean     verify;
    private Boolean     myVote;
    private Date        time;
    private String      url;

    public static AnswerDto answerToAnswerDto(Answer a, UriInfo uri){
        AnswerDto answerDto = new AnswerDto();
        answerDto.body = a.getBody();
        answerDto.myVote = a.getMyVote();
        answerDto.question = answerDto.question;
        answerDto.time = a.getTime();
        answerDto.owner = uri.getBaseUriBuilder().path("/user/").path(String.valueOf(a.getOwner().getId())).build();
        answerDto.verify = a.getVerify();
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


    public URI getOwner() {
        return owner;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public void setOwner(URI owner) {
        this.owner = owner;
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

}