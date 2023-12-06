package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.dto.output.AnswerDto;
import ar.edu.itba.paw.webapp.dto.output.AnswerVoteDto;
import ar.edu.itba.paw.webapp.controller.utils.GenericResponses;
import ar.edu.itba.paw.webapp.controller.utils.PaginationHeaderUtils;
import ar.edu.itba.paw.webapp.dto.input.AnswerCreateDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Path("/answers")
public class AnswersController {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionController.class);
    @Autowired
    private AnswersService as;

    @Autowired
    private UserService us;

    @Autowired
    private SearchService ss;

    @Autowired
    private QuestionService qs;

    @Autowired
    private Commons commons;

    @Context
    private UriInfo uriInfo;

    @Autowired
    private ServletContext servletContext;

//    @Autowired
//    private CommunityService cs;

    @GET
    @Path("/{id}/")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response getAnswer(@PathParam("id") final Long id) {
        final Answer answer = as.findById(id);

        final AnswerDto answerDto = AnswerDto.answerToAnswerDto(answer, uriInfo);
        return Response.ok(new GenericEntity<AnswerDto>(answerDto) {
        })
                .build();

    }

    @GET
    @Path("/")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response getAnswers(@QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("questionId") final long questionId) {


        Question question = qs.findById(questionId);
        //creo que esto lo handelea security ya

        List<AnswerDto> answers= as.findByQuestion(questionId, page-1 ).stream()
                .map(a -> AnswerDto.answerToAnswerDto(a, uriInfo)).collect(Collectors.toList());

        long countAnswers = as.findByQuestionCount(question.getId());

        if (answers.isEmpty())
            return Response.noContent().build();

        Response.ResponseBuilder responseBuilder = Response.ok(new GenericEntity<List<AnswerDto>>(answers) {
        });
        Response response = PaginationHeaderUtils.addPaginationLinks(page, (int) countAnswers, uriInfo.getAbsolutePathBuilder(), responseBuilder , uriInfo.getQueryParameters());
        return response;
   }

    @POST
    @Path("/{id}/verification/")
    public Response verifyAnswer(@PathParam("id") long id) {

        //Si llego aca es xq security lo dejo, entonces soy el owner
        as.verify(id, true);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{id}/verification/")
    public Response unVerifyAnswer(@PathParam("id") long id) {

        as.verify(id, false);

        return Response.noContent().build();

    }



    @GET
    @Path("/{id}/votes")
    public Response getVotesByAnswer(@PathParam("id") Long answerId, @QueryParam("userId") Long userId , @QueryParam("page") @DefaultValue("1") int page) {
        List<AnswerVotes> av = as.findVotesByAnswerId(answerId,userId,page -1);
        long pages = as.findVotesByAnswerIdCount(answerId,userId);


        List<AnswerVoteDto> avDto = av.stream().map( x->(AnswerVoteDto.AnswerVotesToAnswerVoteDto(x , uriInfo) )).collect(Collectors.toList());

        Response.ResponseBuilder res = Response.ok(
                new GenericEntity<List<AnswerVoteDto>>(avDto) {
                });


        return PaginationHeaderUtils.addPaginationLinks(page, (int) pages, uriInfo.getAbsolutePathBuilder(), res , uriInfo.getQueryParameters());

    }

    @GET
    @Path("/{id}/votes/users/{userId}")
    public Response getVote(@PathParam("id") Long answerId, @PathParam("userId") Long userId) {
       AnswerVotes av = as.getAnswerVote(answerId,userId);
        return Response.ok(new GenericEntity<AnswerVoteDto>(AnswerVoteDto.AnswerVotesToAnswerVoteDto(av , uriInfo)) {
        }).build();
    }

    @PUT
    @Path("/{id}/votes/users/{userId}")
    @Consumes(value = { MediaType.APPLICATION_JSON })
    public Response updateVote(@PathParam("id") Long id, @PathParam("userId") Long userId, @QueryParam("vote")   Boolean vote) {

        final User user = us.findById(userId);
        Answer answer = as.findById(id);

        if(vote == null)
            throw new IllegalArgumentException("vote.cannot.be.null");


        as.answerVote(answer, vote, user.getEmail());
        return Response.noContent().build();
        }

    @DELETE
    @Path("/{id}/votes/users/{userId}")
    @Consumes(value = { MediaType.APPLICATION_JSON })
    public Response deleteVote(@PathParam("id") Long id, @PathParam("userId") Long userId) {
        User user = us.findById(userId);
        Answer answer = as.findById(id);
        as.answerVote(answer, null, user.getEmail());
        return Response.noContent().build();
    }

    @POST
    @Path("/")
    @Consumes(value = { MediaType.APPLICATION_JSON })
    public Response create(@Valid final AnswerCreateDto form) {
        final User user = commons.currentUser();

        final String baseUrl = uriInfo.getBaseUriBuilder().replacePath(servletContext.getContextPath()).toString();

        Answer answer = as.create(form.getBody(), user.getEmail(), form.getQuestionId(), baseUrl);

        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(answer.getId())).build();

        return Response.created(uri).build();
    }

    /*
     * @DELETE
     * 
     * @Path("/{id}/")
     * public Response deleteAnswer(@PathParam("id") long id) {
     * Long questionId = as.findById(id).get().getQuestion().getId();
     * as.deleteAnswer(id);
     * return Response.ok().build();
     * }
     */

    @GET
    @Path("/owner")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response userAnswers(@DefaultValue("1") @QueryParam("page") int page,
            @DefaultValue("-1") @QueryParam("userId") int userId) {
        User u = commons.currentUser();
        List<Answer> al = us.getAnswers(u.getId(), page - 1);
        if (al.isEmpty())
            return Response.noContent().build();

        long pages = us.getPageAmountForAnswers(u.getId());
        List<AnswerDto> alDto = al.stream().map(x -> AnswerDto.answerToAnswerDto(x, uriInfo))
                .collect(Collectors.toList());

        Response.ResponseBuilder res = Response.ok(
                new GenericEntity<List<AnswerDto>>(alDto) {
                });

        return PaginationHeaderUtils.addPaginationLinks(page, (int)pages, uriInfo.getAbsolutePathBuilder(), res , uriInfo.getQueryParameters());

    }

    @GET
    @Path("/top")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response topAnswers(
            @DefaultValue("-1") @QueryParam("userId") Integer userId) {
        User u = commons.currentUser();
        if (userId < -1)
            return GenericResponses.badRequest();

        List<Answer> answers = ss.getTopAnswers(u.getId());
        if (answers.isEmpty())
            return Response.noContent().build();
        List<AnswerDto> alDto = answers.stream().map(x -> AnswerDto.answerToAnswerDto(x, uriInfo))
                .collect(Collectors.toList());
        return Response.ok(
                new GenericEntity<List<AnswerDto>>(alDto) {
                })
                .build();
    }

}
