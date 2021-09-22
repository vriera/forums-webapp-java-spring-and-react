package ar.edu.itba.paw.webapp.form;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class AnswersForm {

    @NotEmpty
    @Size(max = 10000)
    private String body;

    public AnswersForm(){}

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

}
