package ar.edu.itba.paw.models;

public class Answer {

    private Long id;
    //Text
    private String body;

    private Long owner;

    private Long id_question;

    public Answer(){

    }
    public Answer(long id, String body, long owner, long id_question) {
        this.id = id;
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

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getOwner() {
        return owner;
    }

    public void setOwner(long owner) {
        this.owner = owner;
    }

    public Long getId_question() { return id_question; }

    public void setId_question(Long id_question) { this.id_question = id_question; }
}
