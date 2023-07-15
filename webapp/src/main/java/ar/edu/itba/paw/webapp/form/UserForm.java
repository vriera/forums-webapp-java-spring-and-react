package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.fields.FieldsValueMatch;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@FieldsValueMatch(field="password", fieldMatch = "repeatPassword")
public class UserForm {

    @NotEmpty
    @NotNull
    @Size(max=250)
    private String username;

    @NotEmpty
    @NotNull
    @Email
    private String email;

    @NotEmpty
    @NotNull
    @Size(max=250)
    private String password;

    public UserForm(){}

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
    }}
