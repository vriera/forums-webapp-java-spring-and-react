package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Entity
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "answer_answer_id_seq")
    @SequenceGenerator(name = "answer_answer_id_seq", sequenceName = "answer_answer_id_seq", allocationSize = 1)
    @Column(name = "answer_id")
    private Long id;
    // Text
    private String body;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User owner;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "question_id")
    private Question question;

    @Transient
    private Long id_question;

    @Column(name = "verify")
    private Boolean verify;

    @Transient
    private int votes;

    @Transient
    private Boolean myVote;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "answer", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<AnswerVotes> answerVotes = new ArrayList<>();

    @Temporal(TemporalType.TIMESTAMP)
    private Date time;

    /* default */
    public Answer() {
        // Just for hibernate

    }

    public Answer(Long id, String body, Boolean verify, Question question, User owner) {
        this.verify = verify;
        this.id = id;
        this.body = body;
        this.owner = owner;
        this.question = question;

    }

    public Answer(Long id, String body, Boolean verify, Question question, User owner, Date time) {
        this.verify = verify;
        this.id = id;
        this.body = body;
        this.owner = owner;
        this.question = question;
        this.time = time;
    }

    public Answer(Long answer_id, String body, Boolean verify, Question question, int votes, User user, Date date) {
        this(answer_id, body, verify, question, user, date);
        this.votes = votes;
    }// jpa

    public Answer(Long id, String body, Boolean verify, Long id_question, User owner) {
        this.verify = verify;
        this.id = id;
        this.body = body;
        this.owner = owner;
        this.id_question = id_question;
    }// jdbc

    public Answer(Long answer_id, String body, Boolean verify, Long question_id, int votes, User user) {
        this(answer_id, body, verify, question_id, user);
        this.votes = votes;
    }// jdbc

    //
    // @PostLoad
    // private void postLoad(){
    // votes = answerVotes.stream().mapToInt(
    // ( x) -> {
    // if(x.getVote() == null)
    // return 0;
    //
    // return x.getVote() ? 1 : -1;
    // }).sum();
    // }

    public Date getTime() {

        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void setVerify(Boolean verify) {
        this.verify = verify;
    }

    public Boolean getVerify() {
        if (verify == null)
            return false;
        return verify;
    }

    public List<AnswerVotes> getAnswerVotes() {
        return answerVotes;
    }

    public void setAnswerVotes(List<AnswerVotes> answerVotes) {
        this.answerVotes = answerVotes;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public Boolean getMyVote() {
        return myVote;
    }
}
