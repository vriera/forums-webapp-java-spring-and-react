package ar.edu.itba.paw.webapp.form;

import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UserForm {

    private Number key;

    @NotNull
    @Size(max=250)
    private String username;

    @NotNull
    @Email
    private String email;

    @NotNull
    @Size(max=250)
    private String password;

    public UserForm(){}

    public Number getKey() {
        return key;
    }

    public void setKey(Number key) {
        this.key = key;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
