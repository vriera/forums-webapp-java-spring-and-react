package ar.edu.itba.paw.webapp.controller.dto.cards;

import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.webapp.controller.dto.Utils.UtilsDto;
import ar.edu.itba.paw.webapp.controller.dto.cards.preview.CommunityPreviewDto;
import ar.edu.itba.paw.webapp.controller.dto.cards.preview.UserPreviewDto;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class QuestionCardDto {



    private long id;
    private String title;
    private UserPreviewDto user;
    private CommunityPreviewDto community;
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

    public UserPreviewDto getUser() {
        return user;
    }

    public void setUser(UserPreviewDto user) {
        this.user = user;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
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

    private String timestamp;
    private long votes;
    private URI questionUri;

    public QuestionCardDto(){};

    public static QuestionCardDto toQuestionCardDto(Question q , UriInfo uri){

        QuestionCardDto qp = new QuestionCardDto();
        qp.setId(q.getId());
        qp.setCommunity(CommunityPreviewDto.toCommunityPreview(q.getForum().getCommunity() , uri));
        qp.setUser(UserPreviewDto.toUserPreview(q.getOwner() , uri));
        qp.setBody(q.getBody());
        qp.setTimestamp(UtilsDto.formatDate(q.getLocalDate()));
        qp.setVotes(q.getVotes());
        URI u = uri.getBaseUriBuilder().path("/question/").path(String.valueOf(q.getId())).build();

        qp.setQuestionUri(u);
        return qp;
    }

}
