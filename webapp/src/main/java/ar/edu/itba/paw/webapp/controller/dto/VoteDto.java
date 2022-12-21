package ar.edu.itba.paw.webapp.controller.dto;

import javax.validation.constraints.NotNull;

public class VoteDto {

    public VoteDto(){

    }

    public boolean isVote() {
        return vote;
    }

    public void setVote(boolean vote) {
        this.vote = vote;
    }
    @NotNull
    private boolean vote;
}
