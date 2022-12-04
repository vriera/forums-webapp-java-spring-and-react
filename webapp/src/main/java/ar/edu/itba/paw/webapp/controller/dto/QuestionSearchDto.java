package ar.edu.itba.paw.webapp.controller.dto;

import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.webapp.controller.dto.previews.QuestionPreviewDto;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class QuestionSearchDto {


    private List<QuestionPreviewDto> questions;

    private String url;
    private Integer totalPages;

    private Integer filter;
    private Integer page;
    private Integer pageSize;

    private Integer order;


    private String query;


    public static QuestionSearchDto QuestionListToQuestionSearchDto(List<Question> qList , UriInfo uri , int community , String query , int filter  , int order, int page , int pageSize , int total){
            QuestionSearchDto  csDto = new QuestionSearchDto();
            List<QuestionPreviewDto> qpList = new ArrayList<>(qList.size());
            for ( Question q : qList){
                QuestionPreviewDto qp = QuestionPreviewDto.toQuestionPreviewDto(q , uri);
                qpList.add(qp);
            }
            csDto.setQuestions(qpList);
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


    public List<QuestionPreviewDto> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionPreviewDto> questions) {
        this.questions = questions;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
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

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }





}
