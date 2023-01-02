package ar.edu.itba.paw.webapp.controller.dto.cards;

import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.webapp.controller.dto.Utils.UtilsDto;
import ar.edu.itba.paw.webapp.controller.dto.cards.preview.CommunityPreviewDto;
import ar.edu.itba.paw.webapp.controller.dto.cards.preview.UserPreviewDto;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Date;

public class QuestionCardDto {



    private long id;
    private String title;
    private UserPreviewDto owner;
    private CommunityPreviewDto community;

    public UserPreviewDto getOwner() {
        return owner;
    }

    public void setOwner(UserPreviewDto owner) {
        this.owner = owner;
    }

    private String body;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public CommunityPreviewDto getCommunity() {
        return community;
    }

    public void setCommunity(CommunityPreviewDto community) {
        this.community = community;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public long getVotes() {
        return votes;
    }

    public void setVotes(long votes) {
        this.votes = votes;
    }

    public URI getQuestionUri() {
        return questionUri;
    }

    public void setQuestionUri(URI questionUri) {
        this.questionUri = questionUri;
    }

    private Date timestamp;
    private long votes;
    private URI questionUri;



    public QuestionCardDto(){};

    public static QuestionCardDto toQuestionCardDto(Question q , UriInfo uri){

        QuestionCardDto qp = new QuestionCardDto();

        qp.setId(q.getId());
        qp.setCommunity(CommunityPreviewDto.toCommunityPreview(q.getForum().getCommunity() , uri));
        qp.setOwner(UserPreviewDto.toUserPreview(q.getOwner() , uri));
        qp.setBody(q.getBody());
        qp.setTimestamp(q.getTime());
        qp.setVotes(q.getVotes());
        qp.setTitle(q.getTitle());
        URI u = uri.getBaseUriBuilder().path("/question/").path(String.valueOf(q.getId())).build();

        qp.setQuestionUri(u);
        return qp;
    }

}
