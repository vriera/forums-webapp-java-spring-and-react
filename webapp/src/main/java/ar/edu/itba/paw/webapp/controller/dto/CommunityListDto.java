package ar.edu.itba.paw.webapp.controller.dto;

import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.webapp.controller.dto.previews.CommunityPreviewDto;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CommunityListDto {


    public List<CommunityPreviewDto> getCommunities() {
        return communities;
    }

    public void setCommunities(List<CommunityPreviewDto> communities) {
        this.communities = communities;
    }

    private List<CommunityPreviewDto> communities;

    private String url;


    public Long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Long totalPages) {
        this.totalPages = totalPages;
    }

    private Long totalPages;
    //private Integer filter;
    private Integer page;
    private Integer pageSize;
    //private Integer order;
    private String query;



    public static CommunityListDto communityListToCommunityListDto(List<Community> cList , UriInfo uri , String query /*, int filter  , int order*/, Integer page , Integer pageSize , Long total){
        CommunityListDto communityListDto = new CommunityListDto();
        List<CommunityPreviewDto> previewList = new ArrayList<>(cList.size());

        for ( Community c : cList){
            CommunityPreviewDto communityPreview = CommunityPreviewDto.toCommunityPreview(c , uri);
            previewList.add(communityPreview);
        }


        communityListDto.setCommunities(previewList);

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
