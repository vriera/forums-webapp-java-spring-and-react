package ar.edu.itba.paw.webapp.controller.dto;

import ar.edu.itba.paw.models.Answer;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class DashboardAnswerListDto {

    private List<URI> answers;

    private String url;



    private Integer totalPages;

    private Integer page;
    private Integer pageSize;


    public static DashboardAnswerListDto answerListToQuestionListDto(List<Answer> aList , UriInfo uri , int page , int pageSize , int totalPages){
        DashboardAnswerListDto alDto = new DashboardAnswerListDto();
        List<URI> aURIlist = new ArrayList<>(aList.size());
        for(Answer a : aList) {
            URI u = uri.getBaseUriBuilder().path("/user").path(String.valueOf(a.getId())).build();
            aURIlist.add(u);
        }
        alDto.answers = aURIlist;
        alDto.url = uri.getBaseUriBuilder().path("/user").path("/answers").build().toString();
        alDto.page = page;
        alDto.pageSize = pageSize;
        alDto.totalPages = totalPages;
        return alDto;
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
