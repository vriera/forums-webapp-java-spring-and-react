package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Question;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class DashboardQuestionListDto {



    private List<URI> answers;

    private String url;
    private Integer totalPages;

    private Integer page;
    private Integer pageSize;




    public static DashboardQuestionListDto questionListToQuestionListDto(List<Question> qList , UriInfo uri , int page , int pageSize , int total){
        DashboardQuestionListDto qlDto = new DashboardQuestionListDto();
        List<URI> qURIList = new ArrayList<>(qList.size());
        for ( Question q : qList){
            URI u = uri.getBaseUriBuilder().path("/questions/").path(String.valueOf(q.getId())).build();
            qURIList.add(u);
        }
        qlDto.answers = qURIList;
        qlDto.url = uri.getBaseUriBuilder().path("/dashboard").path("/questions").build().toString();
        qlDto.page = page;
        qlDto.pageSize = pageSize;
        qlDto.totalPages = total;
        return qlDto;

    }


    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
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
