package ar.edu.itba.paw.webapp.form;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

public class AnswersForm {

    @NotNull
    @Range(min = 1, max = 999999)
    private String body;

    public AnswersForm(){}

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

}
