package ar.edu.itba.paw.models;


public class Question {


    private SmartDate smartDate;

    private String title;

    private String body;

    private User owner;

    private Long id;

    //private String ImagePath;


    //private List<Answers>;

    public Question(){

    }


    public Question(SmartDate smartDate, String title, String body, User owner , Long id) {
        this.id = id;
        this.smartDate = smartDate;
        this.title = title;
        this.body = body;
        this.owner = owner;
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

}
