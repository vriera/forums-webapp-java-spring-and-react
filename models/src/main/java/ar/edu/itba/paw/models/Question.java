package ar.edu.itba.paw.models;

import javax.persistence.*;

@Table(name = "question")
@Entity
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id", nullable = false)
    private Long id;

    @Column(name = "title", length = 250)
    private String title;

    @Lob
    @Column(name = "body")
    private String body;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;

    @ManyToOne
    @JoinColumn(name = "forum_id")
    private Forum forum;

    @Column(name = "\"time\"", nullable = false)
    @Convert(converter = SmartDateConverter.class)
    private SmartDate smartDate;

    @OneToOne
    @JoinColumn(name = "image_id")
    private Image image;
    
    

    public Question(Long id, SmartDate smartDate, String title, String body, User owner, Forum forum , Image image) {
        this.id = id;
        this.smartDate = smartDate;
        this.title = title;
        this.body = body;
        this.owner = owner;
        //this.community = community;
        this.forum = forum;
        this.image = image;
    }

    public Question(long question_id, SmartDate smartDate, String title, String body, int votes, User user, Forum forum , Image image) {
        this(question_id,smartDate,title,body,user,forum,image);
        this.votes=votes;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public SmartDate getSsmartDate() {
        return smartDate;
    }

    public void setSmartDate(SmartDate smartDate) {
        this.smartDate = smartDate;
    }

    public Forum getForum() {
        return forum;
    }

    public void setForum(Forum forum) {
        this.forum = forum;
    }

    public User getUser() {
        return owner;
    }

    public void setUser(User user) {
        this.owner = user;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}