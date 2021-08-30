package ar.edu.itba.paw.models;


public class Question {

    //Timestamp
    private SmartDate smartDate;
    //Varchar
    private String title;
    //Text
    private String body;
    //
    private User owner;
    //serial i
    private Long id;

    private Community community;
    //private String ImagePath;


    //private List<Answers>;

    public Question(){

    }


    public Question(Long id, SmartDate smartDate, String title, String body, User owner , Community community) {
        this.id = id;
        this.smartDate = smartDate;
        this.title = title;
        this.body = body;
        this.owner = owner;
        this.community = community;
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
}
