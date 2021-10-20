package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Table(name = "answer")
@Entity
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id", nullable = false)
    private Long id;

    @Lob
    @Column(name = "body", nullable = false)
    private String body;

    @Column(name = "verify")
    private Boolean verify;

    @ManyToOne(optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Answer(Long id, String body, Boolean verify, Question question, User user, SmartDate time) {
        this.id = id;
        this.body = body;
        this.verify = verify;
        this.question = question;
        this.user = user;
        this.time = time;
    }

    public Answer() {
    }

    @Column(name = "\"time\"", nullable = false)
    @Convert(converter = SmartDateConverter.class)
    private SmartDate time;



    public SmartDate getTime() {
        return time;
    }

    public void setTime(SmartDate time) {
        this.time = time;
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

    public Boolean getVerify() {
        return verify;
    }

    public void setVerify(Boolean verify) {
        this.verify = verify;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}