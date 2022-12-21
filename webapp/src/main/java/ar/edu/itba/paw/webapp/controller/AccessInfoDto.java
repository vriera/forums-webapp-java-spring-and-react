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
    private URI uri;

    public AccessInfoDto(){}
}
