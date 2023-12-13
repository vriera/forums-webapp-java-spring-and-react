package ar.edu.itba.paw.webapp.dto.input;


import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


public class UserUpdateDto {

    @NotEmpty
    @Size(max = 250)
    private String newUsername;


    @NotEmpty
    @Size(max = 250)
    private String newPassword;

    @NotNull
    @NotEmpty
    @Size(max = 250)
    private String currentPassword;

    public UserUpdateDto(){}

    public String getNewUsername() {
        return newUsername;
    }

    public void setNewUsername(String newUsername) {
        this.newUsername = newUsername;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }
}
