package ar.edu.itba.paw.models;

import javax.persistence.*;

//@Entity
public class Answer {


    /*@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="answer_answerid_seq")
    @SequenceGenerator(name="answer_answerid_seq", allocationSize=1)
    @Column(name= "answer_id")
    
     */
    private Long id;
    //Text
    private String body;

    private User owner;

    private Long id_question;

    private Boolean verify;

    private int votes;

    /*default*/
    public Answer(){
        //Just for hibernate

    }
    public Answer(Long id, String body, Boolean verify, Long id_question, User owner) {
        this.verify = verify;
        this.id = id;
        this.body = body;
        this.owner = owner;
        this.id_question = id_question;
    }

    public Answer(Long answer_id, String body, Boolean verify, Long question_id, int votes, User user) {
        this(answer_id,body,verify,question_id,user);
        this.votes=votes;
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
        return verify;
    }

    public Long getId_question() { return id_question; }

    public void setId_question(Long id_question) { this.id_question = id_question; }

    public void setVote(int vote) {
        this.votes = vote;
    }

    public int getVote() {
        return votes;
    }
}
