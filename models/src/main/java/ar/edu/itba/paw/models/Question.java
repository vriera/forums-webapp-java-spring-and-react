package ar.edu.itba.paw.models;

import javax.persistence.*;

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

    @Column(name = "\"time\"", nullable = false)
    @Convert(converter = SmartDateConverter.class)
    private SmartDate smartDate;

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



    public Question(Long id, SmartDate smartDate, String title, String body, User owner, Community community, Forum forum , Long imageId) {
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
        this.community = community;
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



}
