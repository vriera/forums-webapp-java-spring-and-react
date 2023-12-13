package ar.edu.itba.paw.webapp.dto.output;

import ar.edu.itba.paw.models.AccessType;

import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class AccessInfoDto {

    public boolean getCanAccess() {
        return canAccess;
    }

    public void setCanAccess(boolean canAccess) {
        this.canAccess = canAccess;
    }

    private boolean canAccess;

    public boolean isCanAccess() {
        return canAccess;
    }

    public Integer getAccessType() {
        return accessType;
    }

    public void setAccessType(Integer accessType) {
        this.accessType = accessType;
    }

    private Integer accessType;

    public URI getUrl() {
        return url;
    }

    public void setUrl(URI url) {
        this.url = url;
    }

    private URI url;

    public AccessInfoDto() {
    }

    public static AccessInfoDto acessTypeToAccessInfoDto(Boolean access, AccessType at, Number communityId,
            Number userId, UriInfo uriInfo) {
        AccessInfoDto aiDto = new AccessInfoDto();
        aiDto.canAccess = access;
        aiDto.accessType = at.ordinal();
        aiDto.url = uriInfo.getBaseUriBuilder().path("/communities/").path(String.valueOf(communityId)).path("/users/")
                .path(String.valueOf(userId)).path("/access-type").build();
        return aiDto;
    }

    public static AccessInfoDto noTypeAccessInfoDto(Boolean access, Number communityId, Number userId,
            UriInfo uriInfo) {
        AccessInfoDto aiDto = new AccessInfoDto();
        aiDto.canAccess = access;
        aiDto.url = uriInfo.getBaseUriBuilder().path("/communities/").path(String.valueOf(communityId)).path("/users/")
                .path(String.valueOf(userId)).path("/access-type").build();
        return aiDto;
    }
}
