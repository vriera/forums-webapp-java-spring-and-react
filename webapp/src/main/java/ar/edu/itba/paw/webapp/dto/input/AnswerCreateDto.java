package ar.edu.itba.paw.webapp.dto.input;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class AnswerCreateDto {

    @NotEmpty
    @Size(max = 10000)
    @NotNull
    private String body;

    @NotNull
    @Range(min =  0 )
    public Long questionId;
    public AnswerCreateDto(){}

    public String getBody() {
        return body;
    }

    public void setQuestionId(Long questionId){this.questionId = questionId;}
    public Long getQuestionId() {return questionId;}

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "AnswersForm{" +
                "body='" + body + '\'' +
                '}';
    }
}
