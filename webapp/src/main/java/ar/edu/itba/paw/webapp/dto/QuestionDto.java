package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.*;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.sql.Timestamp;

public class QuestionDto {

    private String title;

    private String body;

    private UserDto owner;


    private ForumDto forum;

    private int votes;

    private Boolean myVote;

    private URI image;

    private SmartDate smartDate;

    private String url;

    public static QuestionDto questionDtoToUserDto(Question q, UriInfo uri){
        QuestionDto questionDto = new QuestionDto();
        questionDto.body = q.getBody();
        questionDto.smartDate = q.getSmartDate();
        questionDto.myVote = q.getMyVote();
        questionDto.owner = UserDto.userToUserDto(q.getOwner(),uri);
        questionDto.votes = q.getVotes();
        questionDto.title = q.getTitle();
        questionDto.forum = ForumDto.forumToForumDto(q.getForum(),uri);
        if(q.getImageId()!=null){
            questionDto.image = uri.getBaseUriBuilder().path("/image/").path(String.valueOf(q.getImageId())).build();
        }

        questionDto.url = uri.getAbsolutePathBuilder().path("question").path(String.valueOf(q.getId())).build().toString();
        return questionDto;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setMyVote(Boolean myVote) {
        this.myVote = myVote;
    }

    public void setOwner(UserDto owner) {
        this.owner = owner;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setForum(ForumDto forum) {
        this.forum = forum;
    }

    public void setImage(URI image) {
        this.image = image;
    }

    public void setSmartDate(SmartDate smartDate) {
        this.smartDate = smartDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }


    public Boolean getMyVote() {
        return myVote;
    }

    public UserDto getOwner() {
        return owner;
    }

    public String getBody() {
        return body;
    }

    public String getTitle() {
        return title;
    }

    public int getVotes() {
        return votes;
    }


    public ForumDto getForum() {
        return forum;
    }

    public SmartDate getSmartDate() {
        return smartDate;
    }

    public URI getImage() {
        return image;
    }

    @Override
    public String toString() {
        return "QuestionDto{" +
                "title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", owner=" + owner +
                ", forum=" + forum +
                ", votes=" + votes +
                ", myVote=" + myVote +
                ", image=" + image +
                ", smartDate=" + smartDate.toString() +
                ", url='" + url + '\'' +
                '}';
    }
}
