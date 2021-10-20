package ar.edu.itba.paw.models;


import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "question")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="question_question_id_seq")
    @SequenceGenerator(name="question_question_id_seq", allocationSize=1)
    @Column(name= "question_id")
    private Long id;
    //Timestamp


    @Temporal(TemporalType.TIMESTAMP)
    @Column(name= "time")
    private Date time;

    //Varchar

    private String title;
    //Text

    private String body;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User owner;

    @Column(name= "image_id")
    private Integer imageId;

    @Transient
    private Community community;
    //private String ImagePath;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "forum_id")
    private Forum forum;
    //private List<Answers>;

    @Transient
    private int votes;

    public Question(){

    }



    public Question(long id, Date smartDate, String title, String body, User owner, Community community, Forum forum , Number imageId) {
        this.id = id;
        this.time = smartDate;
        this.title = title;
        this.body = body;
        this.owner = owner;
        this.community = community;
        this.forum = forum;
        this.imageId = (Integer) imageId;
    }

    public Question(String title, String body , long communityId , long forumId){
        this.title = title;
        this.body = body;
        this.community = new Community(communityId , "sample community", "Sample description");
        this.forum = new Forum(forumId , "sample name" , community);
        this.owner = new User();
        this.time = new Date();
    }

    public Question(long question_id, Date time, String title, String body, int votes, User user, Community community, Forum forum , Number imageId) {
        this(question_id,time,title,body,user,community,forum,imageId);
        this.votes=votes;
    }


    public Forum getForum() {
        return forum;
    }

    public void setForum(Forum forum) {
        this.forum = forum;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /*/public SmartDate getSmartDate() {
        return smartDate;
    }

    public void setSmartDate(SmartDate smartDate) {
        this.smartDate = smartDate;
    }

     */

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
        this.community = community;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public Number getImageId() {
        return imageId;
    }

    public void setImageId(Number imageId) {
        this.imageId = (Integer) imageId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }


}
