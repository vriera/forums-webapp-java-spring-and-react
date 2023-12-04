package ar.edu.itba.paw.webapp.dto.output;

import ar.edu.itba.paw.models.QuestionVotes;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class QuestionVoteDto
{
    private URI url;

    private Boolean vote;

    private URI question;

    private URI user;

    public  static QuestionVoteDto questionVotesToQuestionVoteDto(QuestionVotes vote , UriInfo uri){
        QuestionVoteDto voteDto = new QuestionVoteDto();
        String questionId = String.valueOf(vote.getQuestion().getId());
        String userId = String.valueOf(vote.getOwner().getId());
        voteDto.question = uri.getBaseUriBuilder().path("/questions/").path(questionId).build();
        voteDto.vote = vote.getVote();
        voteDto.user= uri.getBaseUriBuilder().path("/users/").path(userId).build();
        voteDto.url= uri.getBaseUriBuilder().path("/questions/").path(questionId).path("/votes/users/").path(userId).build();
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

    public URI getQuestion() {
        return question;
    }

    public void setQuestion(URI question) {
        this.question = question;
    }

    public URI getUser() {
        return user;
    }

    public void setUser(URI user) {
        this.user = user;
    }

}
