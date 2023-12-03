package ar.edu.itba.paw.webapp.dto.input;

import org.hibernate.validator.constraints.Email;

public class InviteDto {

    @Email
    private String email;

    public InviteDto(){}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
