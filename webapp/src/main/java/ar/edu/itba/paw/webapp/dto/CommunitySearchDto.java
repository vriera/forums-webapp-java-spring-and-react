package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Question;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CommunitySearchDto {


    private List<URI> answers;

    private String url;



    private Integer totalPages;

    private Integer filter;
    private Integer page;
    private Integer pageSize;
    private Integer order;


    private String query;


    public static CommunitySearchDto QuestionListToCommunitySearchDto(List<Question> qList , UriInfo uri , int community , String query , int filter  , int order, int page , int pageSize , int total){
            CommunitySearchDto  csDto = new CommunitySearchDto();
            List<URI> qURIList = new ArrayList<>(qList.size());
            for ( Question q : qList){
                URI u = uri.getBaseUriBuilder().path("/questions/").path(String.valueOf(q.getId())).build();
                qURIList.add(u);
            }
            csDto.setAnswers(qURIList);

            MultivaluedMap<String,String> params = uri.getQueryParameters();
            UriBuilder uriB = uri.getAbsolutePathBuilder();
            Set<String> keys = params.keySet();
            for (String key: keys) {
                uriB.queryParam(key , params.getFirst(key));
            }
            csDto.setUrl(uriB.toString());
            csDto.setQuery(query);
            csDto.setFilter(filter);
            csDto.setOrder(order);
            csDto.setPage(page);
            csDto.setPageSize(pageSize);
            csDto.setTotalPages(total);
            return csDto;

    }


    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }




    public Integer getFilter() {
        return filter;
    }

    public void setFilter(Integer filter) {
        this.filter = filter;
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

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }


    public List<URI> getAnswers() {
        return answers;
    }

    public void setAnswers(List<URI> answers) {
        this.answers = answers;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }





}
