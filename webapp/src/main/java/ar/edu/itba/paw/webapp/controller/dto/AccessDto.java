package ar.edu.itba.paw.webapp.controller.dto;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.net.URI;

public class AccessDto {

    @NotNull
    @NotEmpty
    private String accessType;

    private URI uri;

    public AccessDto(){};


    public String getAccessType() {
        return accessType;
    }

    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }


}
