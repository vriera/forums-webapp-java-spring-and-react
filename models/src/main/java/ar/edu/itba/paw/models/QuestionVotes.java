package ar.edu.itba.paw.models;

import javax.persistence.*;

@Entity
public class QuestionVotes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY , generator = "questionvotes_votes_id_seq")
    @SequenceGenerator(name = "questionvotes_votes_id_seq" , sequenceName = "questionvotes_votes_id_seq" , allocationSize = 1)
    @Column(name = "votes_id", nullable = false)
    private Long id;
    
    private Boolean vote;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User owner;

    public QuestionVotes() {
    }

    public QuestionVotes(Long id, Boolean vote, User owner, Question question){
        this.id=id;
        this.vote = vote;
        this.owner = owner;
        this.question = question;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public User getOwner() {
        return owner;
    }

    public void setId(Long id) {
        this.id = id;
    }



    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Boolean getVote() {
        return vote;
    }

    public void setVote(Boolean vote) {
        this.vote = vote;
    }

    public Long getId() {
        return id;
    }
}