package ar.edu.itba.paw.models;

import javax.persistence.*;

@Table(name = "questionvotes")
@Entity
public class Questionvote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "votes_id", nullable = false)
    private Integer id;

    @Column(name = "vote")
    private Boolean vote;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Questionvote() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}