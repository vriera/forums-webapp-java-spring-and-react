package ar.edu.itba.paw.webapp.dto.input;

import javax.validation.constraints.NotNull;

public class VoteDto {

    public Boolean getVote() {
        return vote;
    }

    public void setVote(Boolean vote) {
        this.vote = vote;
    }

    @NotNull(message = "vote.null")
    private Boolean vote;


    public VoteDto(){};

}
