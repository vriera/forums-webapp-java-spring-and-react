package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Question;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CommunityListDto {


    private List<URI> communities;

    private String url;



    private Integer totalPages;
    //private Integer filter;
    private Integer page;
    private Integer pageSize;
    //private Integer order;
    private String query;

    public static CommunityListDto communityListToCommunityListDto(List<Community> cList , UriInfo uri , String query /*, int filter  , int order*/, Integer page , Integer pageSize , Integer total){
        CommunityListDto communityListDto = new CommunityListDto();
        List<URI> cURIList = new ArrayList<>(cList.size());

        for ( Community c : cList){
            URI u = uri.getBaseUriBuilder().path("/community/").path(String.valueOf(c.getId())).build();
            cURIList.add(u);
        }


        communityListDto.setCommunities(cURIList);

        MultivaluedMap<String,String> params = uri.getQueryParameters();

        UriBuilder uriB = uri.getAbsolutePathBuilder();
        Set<String> keys = params.keySet();
        for (String key: keys) {
            uriB.queryParam(key , params.getFirst(key));
        }
        communityListDto.setUrl(uriB.toString());

        if( query != null && !query.equals("")) {
            communityListDto.setQuery(query);
        }

        //csDto.setFilter(filter);
        //csDto.setOrder(order);
        communityListDto.setPage(page);
        communityListDto.setPageSize(pageSize);
        communityListDto.setTotalPages(total);

        return communityListDto;

    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }




    public List<URI> getCommunities() {
        return communities;
    }

    public void setCommunities(List<URI> communities) {
        this.communities = communities;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
