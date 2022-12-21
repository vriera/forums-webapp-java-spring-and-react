package ar.edu.itba.paw.webapp.controller;

import java.net.URI;

public class AccessInfoDto {

    public boolean getCanAccess() {
        return canAccess;
    }

    public void setCanAccess(boolean canAccess) {
        this.canAccess = canAccess;
    }

    private boolean canAccess;

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    private URI uri;

    public AccessInfoDto(){}
}
