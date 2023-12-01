package ar.edu.itba.paw.webapp.controller.form;

import ar.edu.itba.paw.webapp.controller.form.validation.fields.FieldsValueMatch;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

import javax.validation.constraints.Size;

//
//@FieldsValueMatch(field="password", fieldMatch = "repeatPassword")
public class UserForm {

    @NotEmpty
    @Size(max=250)
    private String username;

    @NotNull
    @NotEmpty
    @Email
    private String email;

    @NotNull
    @NotEmpty

    @Size(max=250)
    private String password;

    public UserForm(){ System.out.println("CREANDO UN USER FORM BEAN");}

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
