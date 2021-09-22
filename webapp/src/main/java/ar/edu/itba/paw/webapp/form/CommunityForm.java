package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CommunityForm {
    @NotNull
    @Size(max = 250)
    private String name;

    @NotNull
    @Size(max = 2500)
    private String description;

    public CommunityForm(){

    }


    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription(){
        return description;
    }

}
