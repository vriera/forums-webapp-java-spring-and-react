package ar.edu.itba.paw.models;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "question")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="question_question_id_seq")
    @SequenceGenerator(name="question_question_id_seq" , sequenceName = "question_question_id_seq", allocationSize=1)
    @Column(name= "question_id")
    private Long id;

    private String title;

    private String body;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User owner;

    @Column(name= "image_id")
    private Long imageId;

    public Timestamp getLocalDate() {
        return localDate;
    }

    public void setLocalDate(Timestamp localDate) {
        this.smartDate = new SmartDate(localDate);
        this.localDate = localDate;
    }

    @CreationTimestamp
    @Column(name = "\"time\"", nullable = false)
    private Timestamp localDate;

    @Transient
    private SmartDate smartDate;

    @Transient
    private Community community;
    //private String ImagePath;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "forum_id")
    private Forum forum;
    //private List<Answers>;

    @Transient
    private int votes;

    @Transient
    private Boolean myVote;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "question",cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<QuestionVotes> questionVotes = new ArrayList<>();

    public Question(){

    }


    public Question(Long id, Timestamp date, String title, String body, User owner, Community community, Forum forum , Long imageId) {
       this(id , new SmartDate(date) , title , body , owner , community , forum , imageId);
    }


    public Question(Long id, SmartDate smartDate, String title, String body, User owner, Community community, Forum forum , Long imageId)
    {
        this.localDate = smartDate.getTime();
        this.id = id;
        this.smartDate = smartDate;
        this.title = title;
        this.body = body;
        this.owner = owner;
        this.community = forum.getCommunity();
        this.forum = forum;
        this.imageId = imageId;
    }


    public Question(Long question_id, SmartDate time, String title, String body, int votes, User user, Community community, Forum forum , Long imageId) {
        this(question_id,time,title,body,user, forum.getCommunity(), forum,imageId);
        this.votes=votes;
    }



    public Forum getForum() {
        return forum;
    }

    public void setForum(Forum forum) {
        this.forum = forum;
        this.community = forum.getCommunity();
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SmartDate getSmartDate() {
        return smartDate;
    }

    public void setSmartDate(SmartDate smartDate) {
        this.localDate = smartDate.getTime();
        this.smartDate = smartDate;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = this.forum.getCommunity();
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public SmartDate getTime() {
        return smartDate;
    }

    public void setQuestionVotes(List<QuestionVotes> questionVotes) {
        this.questionVotes = questionVotes;
    }

    public void setMyVote(Boolean myVote) {
        this.myVote = myVote;
    }

    public Boolean getMyVote() {
        return myVote;
    }

    public List<QuestionVotes> getQuestionVotes() {
        return questionVotes;
    }

    @PostLoad
    private void postLoad(){
        for(QuestionVotes vote : questionVotes){
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

    public void getAnswerVote(User user){
        if(user == null) return;
        for(QuestionVotes qv : questionVotes){
            if(qv.getOwner().equals(user)){
                myVote =  qv.getVote();
                return;
            }
        }
        myVote = null;
    }
}
