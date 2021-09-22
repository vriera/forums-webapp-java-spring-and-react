package ar.edu.itba.paw.models;

public class Answer {


    private Long id;
    //Text
    private String body;

    private User owner;

    private Long id_question;

    private Boolean verify;

    private int votes;

    public Answer(){

    }
    public Answer(long id, String body, Boolean verify, long id_question, User owner) {
        this.verify = verify;
        this.id = id;
        this.body = body;
        this.owner = owner;
        this.id_question = id_question;
    }

    public Answer(long answer_id, String body, boolean verify, long question_id, int votes, User user) {
        this(answer_id,body,verify,question_id,user);
        this.votes=votes;
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

    public Long getId_question() { return id_question; }

    public void setId_question(Long id_question) { this.id_question = id_question; }

    public void setVote(int vote) {
        this.votes = vote;
    }

    public int getVote() {
        return votes;
    }
}
