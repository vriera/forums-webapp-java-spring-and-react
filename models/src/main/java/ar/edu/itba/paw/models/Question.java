package ar.edu.itba.paw.models;


public class Question {

    private Long id;
    //Timestamp
    private SmartDate smartDate;
    //Varchar
    private String title;
    //Text
    private String body;

    private User owner;


    private Community community;
    //private String ImagePath;

    private Forum forum;
    //private List<Answers>;

    public Question(){

    }


    public Question(long id, SmartDate smartDate, String title, String body, User owner, Community community, Forum forum) {
        this.id = id;
        this.smartDate = smartDate;
        this.title = title;
        this.body = body;
        this.owner = owner;
        this.community = community;
        this.forum = forum;
    }

    public Question(String title, String body , long communityId , long forumId){
        this.title = title;
        this.body = body;
        this.community = new Community(communityId , "sample community", "Sample description");
        this.forum = new Forum(forumId , "sample name" , community);
        this.owner = new User();
        this.smartDate = new SmartDate();
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
}
