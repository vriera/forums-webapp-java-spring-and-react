package ar.edu.itba.paw.models;

import javax.persistence.*;

public class AnswerVotes {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="answervotes_votes_id_seq")
    @SequenceGenerator(name="answervotes_votes_id_seq", allocationSize=1)
    @Column(name= "votes_id")
    private Long id;


    private Boolean vote;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User owner;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "answer_id")
    private Answer answer;

    public AnswerVotes(){}

    public AnswerVotes(Long id, Boolean vote, User owner, Answer answer){
        this.id=id;
        this.vote = vote;
        this.owner = owner;
        this.answer = answer;
    }

    public User getOwner() {
        return owner;
    }

    public Boolean getVote() {
        return vote;
    }

    public Long getId() {
        return id;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setVote(Boolean vote) {
        this.vote = vote;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }
}
