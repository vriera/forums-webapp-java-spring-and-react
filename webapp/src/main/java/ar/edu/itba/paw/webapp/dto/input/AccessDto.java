package ar.edu.itba.paw.webapp.dto.input;

import ar.edu.itba.paw.webapp.dto.validation.ValidAccessType;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

public class AccessDto {

    public String getAccessType() {
        return accessType;
    }

    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }

    @ValidAccessType
    @NotNull
    @NotEmpty
    private String accessType;

    public AccessDto() {
    }
}
