package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.fieldsValueMatch.FieldsValueMatch;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;

@FieldsValueMatch(field="password", fieldMatch = "repeatPassword")
public class UserForm {

    @NotEmpty
    @Size(max=250)
    private String username;

    @NotEmpty
    @Email
    private String email;

    @NotEmpty
    @Size(max=250)
    private String password;

    @NotEmpty
    @Size(max=250)
    private String repeatPassword;

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
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }
}
