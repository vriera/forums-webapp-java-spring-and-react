package ar.edu.itba.paw.webapp.form;

import org.hibernate.validator.constraints.NotEmpty;

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

    @Override
    public String toString() {
        return "AnswersForm{" +
                "body='" + body + '\'' +
                '}';
    }
}
