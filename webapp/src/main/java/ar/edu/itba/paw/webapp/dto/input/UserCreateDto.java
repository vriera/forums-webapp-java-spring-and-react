package ar.edu.itba.paw.webapp.dto.input;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;

public class UserCreateDto {

    @NotEmpty(message = "NotEmpty.userForm.username")
    @Size(max = 250, message = "Size.userForm.username")
    private String username;

    @NotEmpty(message = "NotEmpty.userForm.email")
    @Email(message = "Email.userForm.email")
    @Size(max = 250, message = "Size.userForm.email")
    private String email;

    @NotEmpty(message = "NotEmpty.userForm.password")
    @Size(max = 250, message = "Size.userForm.password")
    private String password;

    public UserCreateDto() {
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
