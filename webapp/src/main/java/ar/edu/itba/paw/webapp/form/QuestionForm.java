package ar.edu.itba.paw.webapp.form;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
//import org.springframework.web.bind.annotation.ModelAttribute;

//import javax.validation.constraints.Size;

public class QuestionForm {
    @NotEmpty
    @Size(  max = 250 )
    private String title;

    @NotEmpty
    @NotNull
    @Size( max = 2500)
    private String body;

    private String image;

    @NotNull
    private Number community;
    //private String ImagePath;

    public Number getCommunity() {
        return community;
    }

    public void setCommunity(Number community) {
        this.community = community;
    }

    public Number getForum() {
        return forum;
    }

    public void setForum(Number forum) {
        this.forum = forum;
    }

    private Number forum;


    public QuestionForm(){

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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
