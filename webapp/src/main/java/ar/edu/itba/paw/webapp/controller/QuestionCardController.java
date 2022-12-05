package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.SearchFilter;
import ar.edu.itba.paw.models.SearchOrder;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.controller.dto.DashboardQuestionListDto;
import ar.edu.itba.paw.webapp.controller.dto.QuestionSearchDto;
import ar.edu.itba.paw.webapp.controller.dto.cards.QuestionCardDto;
import ar.edu.itba.paw.webapp.controller.utils.GenericResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.stream.Collectors;


@Component
@Path("question-cards")
public class QuestionCardController {

    @Context
    private UriInfo uriInfo;

    @Autowired
    private SearchService ss;
    @Autowired
    private Commons commons;

    @GET
    @Path("")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response allPosts(
            @DefaultValue("") @QueryParam("query") String query,
            @DefaultValue("0") @QueryParam("filter") int filter,
            @DefaultValue("0") @QueryParam("order") int order,
            @DefaultValue("0") @QueryParam("page") int page,
            @DefaultValue("10") @QueryParam("size") int size,
            @DefaultValue("-1") @QueryParam("communityId") int communityId
    ) {
        //NO SE SI EL SIZE me puede romper el back!
        //@ModelAttribute("paginationForm") PaginationForm paginationForm)

        int offset = (page) * size;
        int limit = size;
        if(size < 1)
            size =1;

        User u = commons.currentUser();


        List<Question> questionList = ss.search(query, SearchFilter.values()[filter], SearchOrder.values()[order], communityId, u, limit, offset);


        int questionCount = ss.countQuestionQuery(query, SearchFilter.values()[filter], SearchOrder.values()[order], communityId, u);

        int pages = (int) Math.ceil((double) questionCount / size);

        List<QuestionCardDto> qlDto = questionList.stream().map(x -> QuestionCardDto.toQuestionCardDto(x , uriInfo) ).collect(Collectors.toList());
        return Response.ok(
                new GenericEntity<List<QuestionCardDto>>(qlDto) {
                }
        ).build();
    }

}
