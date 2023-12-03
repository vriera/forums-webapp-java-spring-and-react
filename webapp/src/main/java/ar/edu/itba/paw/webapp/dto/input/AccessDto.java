package ar.edu.itba.paw.webapp.dto.input;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

public class AccessDto {

    public String getAccessType() {
        return accessType;
    }

    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }

    @NotNull
    @NotEmpty
    private String accessType;

    public AccessDto(){};
}
