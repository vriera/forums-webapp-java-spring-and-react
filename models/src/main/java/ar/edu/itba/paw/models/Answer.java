package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@Entity
public class Answer {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="answer_answer_id_seq")
    @SequenceGenerator(name="answer_answer_id_seq", allocationSize=1)
    @Column(name= "answer_id")
    private Long id;
    //Text
    private String body;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id")
    private Question question;


    @Transient
    private Long id_question;


    @Column(name= "verify")
    private Boolean verify;

    @Transient
    private int votes;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "answer",cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<AnswerVotes> answerVotes = new ArrayList<>();

    /*

    @Column(name = "\"time\"", nullable = false)
    @Convert(converter = SmartDateConverter.class)
    private SmartDate time;

     */


    /*default*/
    public Answer(){
        //Just for hibernate

    }


    public Answer(Long id, String body, Boolean verify, Question question, User owner) {
        this.verify = verify;
        this.id = id;
        this.body = body;
        this.owner = owner;
        this.question = question;
    }//jpa


    public Answer(Long answer_id, String body, Boolean verify, Question question, int votes, User user) {
        this(answer_id,body,verify,question,user);
        this.votes=votes;
    }//jpa




    public Answer(Long id, String body, Boolean verify, Long id_question, User owner) {
        this.verify = verify;
        this.id = id;
        this.body = body;
        this.owner = owner;
        this.id_question = id_question;
    }//jdbc

    public Answer(Long answer_id, String body, Boolean verify, Long question_id, int votes, User user) {
        this(answer_id,body,verify,question_id,user);
        this.votes=votes;
    }//jdbc




    @PostLoad
    private void postLoad(){
        for(AnswerVotes vote : answerVotes){
           if(vote.getVote() != null){
               if(vote.getVote().equals(true)){
                   votes+=1;
               }else{
                   if(vote.getVote().equals(false)){
                       votes-=1;
                   }
               }
           }

        }
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
        return verify;
    }

    /*

    public Long getId_question() { return id_question; }

    public void setId_question(Long id_question) { this.id_question = id_question; }

     */

    public void setVote(int vote) {
        this.votes = vote;
    }

    public int getVote() {
        return votes;
    }

    public List<AnswerVotes> getAnswerVotes() {
        return answerVotes;
    }

    public void setAnswerVotes(List<AnswerVotes> answerVotes) {
        this.answerVotes = answerVotes;
    }

    public Boolean getAnswerVote(User user){
        for(AnswerVotes av : answerVotes){
            if(av.getOwner().equals(user)){
                return  av.getVote();
            }
        }
        return null;
    }


}
