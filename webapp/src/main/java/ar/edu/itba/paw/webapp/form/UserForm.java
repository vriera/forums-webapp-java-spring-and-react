package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.models.User;

public class UserForm {

    private Number key;

    private String name;

    private String email;

    public Number getKey() {
        return key;
    }

    public void setKey(Number key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserForm(){};
}
