package ar.edu.itba.paw.webapp.dto.output;

import ar.edu.itba.paw.models.AnswerVotes;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class AnswerVoteDto
{
    private URI url;

    private Boolean vote;

    private URI answer;

    private URI user;


    public  static AnswerVoteDto AnswerVotesToAnswerVoteDto(AnswerVotes vote , UriInfo uri){
        AnswerVoteDto voteDto = new AnswerVoteDto();
        String answerId = String.valueOf(vote.getAnswer().getId());
        String userId = String.valueOf(vote.getOwner().getId());
        voteDto.answer = uri.getBaseUriBuilder().path("/answers/").path(answerId).build();
        voteDto.vote = vote.getVote();
        voteDto.user= uri.getBaseUriBuilder().path("/users/").path(userId).build();
        voteDto.url= uri.getBaseUriBuilder().path("/answers/").path(answerId).path("/votes/").path(userId).build();
        return voteDto;
    }

    public URI getUrl() {
        return url;
    }

    public void setUrl(URI url) {
        this.url = url;
    }

    public Boolean getVote() {
        return vote;
    }

    public void setVote(Boolean vote) {
        this.vote = vote;
    }

    public URI getAnswer() {
        return answer;
    }

    public void setAnswer(URI answer) {
        this.answer = answer;
    }

    public URI getUser() {
        return user;
    }

    public void setUser(URI user) {
        this.user = user;
    }



}
