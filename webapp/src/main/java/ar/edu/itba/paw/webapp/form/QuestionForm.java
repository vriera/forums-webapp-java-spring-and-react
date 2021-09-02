package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.SmartDate;
import ar.edu.itba.paw.models.User;
//import org.springframework.web.bind.annotation.ModelAttribute;

//import javax.validation.constraints.Size;

public class QuestionForm {

   // @Size(  max = 250 )
    private String title;

   // @Size( max = 2500)
    private String body;

   // @Size(  max = 250 )
    private String user;

    private Number forum;

    public QuestionForm(){}

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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Number getForum() {
        return forum;
    }

    public void setForum(Number forum) {
        this.forum = forum;
    }
}
