package ar.edu.itba.paw.webapp.controller.dto.previews;

import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.webapp.controller.dto.Utils.UtilsDto;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class QuestionPreviewDto {

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

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    private long id;
    private String title;

    public URI getOwner() {
        return owner;
    }

    public void setOwner(URI owner) {
        this.owner = owner;
    }

    private URI owner;

    public URI getCommunity() {
        return community;
    }

    public void setCommunity(URI community) {
        this.community = community;
    }

    private URI community;
    private String body;
    private String timestamp;
    private long votes;
    private URI uri;
    public QuestionPreviewDto(){};

    public static QuestionPreviewDto toQuestionPreviewDto(Question q , UriInfo uri){

        QuestionPreviewDto qp = new QuestionPreviewDto();
        qp.setId(q.getId());
        qp.setOwner(uri.getBaseUriBuilder().path("/user/").path(String.valueOf(q.getOwner().getId())).build());

        qp.setCommunity(uri.getBaseUriBuilder().path("/community/").path(String.valueOf(q.getForum().getCommunity().getId())).build());

        qp.setBody(q.getBody());

        qp.setTimestamp(UtilsDto.formatDate(q.getLocalDate()));
        qp.setVotes(q.getVotes());
        URI u = uri.getBaseUriBuilder().path("/questions/").path(String.valueOf(q.getId())).build();
        qp.setUri(u);
        return qp;
    }

}
