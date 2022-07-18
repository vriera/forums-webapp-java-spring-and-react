package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.User;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UserListDto {


    private String url;



    private Long pages;
    private Long pageSize;
    private Long totalPages;
    private List<URI> userList;


    public static UserListDto userListToUserListDto(List<User> users , UriInfo uri , Long page , Long pageSize , Long totalPages){
        UserListDto uldto = new UserListDto();
        uldto.userList = new ArrayList<>(users.size());
        for (User u : users ) {
            uldto.userList.add(uri.getBaseUriBuilder().path("/users/").path(String.valueOf(u.getId())).build());
        }

        MultivaluedMap<String,String> params = uri.getQueryParameters();

        UriBuilder uriB = uri.getAbsolutePathBuilder();
        Set<String> keys = params.keySet();
        for (String key: keys) {
            uriB.queryParam(key , params.getFirst(key));
        }
        uldto.setUrl(uriB.toString());
        uldto.setPages(page);
        uldto.setPageSize(pageSize);
        uldto.setTotalPages(totalPages);
        return uldto;
    }



    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getPages() {
        return pages;
    }

    public void setPages(Long pages) {
        this.pages = pages;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }

    public Long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Long totalPages) {
        this.totalPages = totalPages;
    }


}
