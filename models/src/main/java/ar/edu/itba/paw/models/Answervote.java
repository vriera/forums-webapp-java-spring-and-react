package ar.edu.itba.paw.models;

import javax.persistence.*;

@Table(name = "answervotes")
@Entity
public class Answervote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "votes_id", nullable = false)
    private Long id;

    @Column(name = "vote")
    private Boolean vote;

    @ManyToOne
    @JoinColumn(name = "answer_id")
    private Answer answer;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Answervote() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
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

    public void setId(Long id) {
        this.id = id;
    }
}