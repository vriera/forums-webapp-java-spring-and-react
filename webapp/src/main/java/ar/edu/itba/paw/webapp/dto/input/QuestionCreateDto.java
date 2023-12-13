package ar.edu.itba.paw.webapp.dto.input;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

public class QuestionCreateDto {

    @NotEmpty
    @Range(min = 1)
    private Long communityId;
    @NotEmpty

    private String body;
    @NotEmpty
    private String title;

    public QuestionCreateDto() {
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
