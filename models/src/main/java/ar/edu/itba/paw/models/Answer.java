package ar.edu.itba.paw.models;

public class Answer {

    private Long id;
    //Varchar
    private String title;
    //Text
    private String body;

    private User owner;

    private Long id_question;

    public Answer(){

    }
    public Answer(long id, String title, String body, User owner, long id_question) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.owner = owner;
        this.id_question = id_question;
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

    public Long getId_question() { return id_question; }

    public void setId_question(Long id_question) { this.id_question = id_question; }
}
