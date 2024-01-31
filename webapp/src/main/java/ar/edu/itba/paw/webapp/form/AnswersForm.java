package ar.edu.itba.paw.webapp.form;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class AnswersForm {

    @NotNull
    @Min(0)
    private Long questionId;
    @NotEmpty
    @Size(max = 10000)
    @NotNull
    private String body;

    public AnswersForm(){}

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    @Override
    public String toString() {
        return "AnswersForm{" +
                "body='" + body + '\'' +
                '}';
    }
}
