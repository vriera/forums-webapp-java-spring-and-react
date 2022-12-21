package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.SearchFilter;
import ar.edu.itba.paw.models.SearchOrder;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.controller.dto.DashboardQuestionListDto;
import ar.edu.itba.paw.webapp.controller.dto.QuestionSearchDto;
import ar.edu.itba.paw.webapp.controller.dto.cards.QuestionCardDto;
import ar.edu.itba.paw.webapp.controller.utils.GenericResponses;
import ar.edu.itba.paw.webapp.controller.utils.PaginationHeaderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.stream.Collectors;


@Component
@Path("question-cards")
public class QuestionCardController {
    private final static int PAGE_SIZE = 10;
    @Context
    private UriInfo uriInfo;

    @Autowired
    private SearchService ss;
    @Autowired
    private UserService us;
    @Autowired
    private Commons commons;

    @GET
    @Path("")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response searchQuestions(
            @DefaultValue("") @QueryParam("query") String query,
            @DefaultValue("0") @QueryParam("filter") int filter,
            @DefaultValue("0") @QueryParam("order") int order,
            @DefaultValue("1") @QueryParam("page") int page,
            @DefaultValue("-1") @QueryParam("communityId") int communityId,
            @DefaultValue("-1") @QueryParam("requesterId") Integer userId
    ) {
        //NO SE SI EL SIZE me puede romper el back!
        //@ModelAttribute("paginationForm") PaginationForm paginationForm)
        int size = PAGE_SIZE;
        int offset = (page -1) *size ;
        int limit = size;

        User u = commons.currentUser();
        if(  !(userId == -1) && ( u != null && u.getId() != userId ) )
            return GenericResponses.notAuthorized();

        if(userId == -1)
            u=null;

        List<Question> questionList = ss.search(query, SearchFilter.values()[filter], SearchOrder.values()[order], communityId, u, limit, offset);


        int questionCount = ss.countQuestionQuery(query, SearchFilter.values()[filter], SearchOrder.values()[order], communityId, u);

        int pages = (int) Math.ceil((double) questionCount / size);

        List<QuestionCardDto> qlDto = questionList.stream().map(x -> QuestionCardDto.toQuestionCardDto(x , uriInfo) ).collect(Collectors.toList());
        Response.ResponseBuilder res = Response.ok(new GenericEntity<List<QuestionCardDto>>(qlDto) {});
        UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
        if(!query.equals(""))
            uriBuilder.queryParam("query" , query);
        if(filter != 0)
            uriBuilder.queryParam("filter" , filter);
        if(order != 0)
            uriBuilder.queryParam("order" , order);
        if(communityId != -1)
            uriBuilder.queryParam("communityId" , communityId);
        if(userId != -1)
            uriBuilder.queryParam("requesterId" , userId);
        return PaginationHeaderUtils.addPaginationLinks(page , pages,uriBuilder  , res);
    }


    @GET
    @Path("/owned")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response ownedQuestions(
            @DefaultValue("1") @QueryParam("page") int page,
            @DefaultValue("-1") @QueryParam("requestorId") Integer userId
    ) {
        //NO SE SI EL SIZE me puede romper el back!
        //@ModelAttribute("paginationForm") PaginationForm paginationForm)
        int size = 5;
        int offset = (page -1) *size ;
        int limit = size;

        User u = commons.currentUser();
        if(  u == null )
            return GenericResponses.notAuthorized();
        if(u.getId() != userId )
            return GenericResponses.cantAccess();
        if(userId <-1)
            return GenericResponses.badRequest();

        List<Question> questionList = us.getQuestions(userId , page -1);
        int pages = us.getPageAmountForQuestions(userId);

//        int pages = (int) Math.ceil((double) count / size);

        List<QuestionCardDto> qlDto = questionList.stream().map(x -> QuestionCardDto.toQuestionCardDto(x , uriInfo) ).collect(Collectors.toList());
        Response.ResponseBuilder res = Response.ok(new GenericEntity<List<QuestionCardDto>>(qlDto) {});
        UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();

        if(userId != -1)
            uriBuilder.queryParam("requestorId" , userId);
        return PaginationHeaderUtils.addPaginationLinks(page , pages,uriBuilder  , res);
    }


}
