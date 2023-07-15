package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.util.*;

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

    @Temporal(TemporalType.TIMESTAMP)
    private Date time;

    @Transient
    private Community community;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "forum_id")
    private Forum forum;

    @Transient
    private int votes;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "question",cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<QuestionVotes> questionVotes = new TreeSet<>();


    @OneToMany(fetch = FetchType.EAGER, mappedBy = "question",cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Answer> answers = new ArrayList<>();



    public Question(){

    }

    public Question(Long id, Date time, String title, String body, User owner, Forum forum , Long imageId)
    {
        this.id = id;
        this.title = title;
        this.body = body;
        this.time = time;
        this.owner = owner;
        this.community = forum.getCommunity();
        this.forum = forum;
        this.imageId = imageId;
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
        return this.getForum().getCommunity();
    }

    public void setCommunity(Community community) {
        this.community = community;
        this.forum.setCommunity(community);
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


    public void setQuestionVotes(Set<QuestionVotes> questionVotes) {
        this.questionVotes = questionVotes;
    }

    public Set<QuestionVotes> getQuestionVotes() {
        return questionVotes;
    }




    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

//    //lo pasaria al service o investigaria mas
//    @PostLoad
//    private void postLoad(){
//        votes = questionVotes.stream().mapToInt(
//                ( x) -> {
//                    if(x.getVote() == null)
//                        return 0;
//
//                    return x.getVote() ? 1 : -1;
//                }).sum();
//    }


}
