package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.QuestionService;
import ar.edu.itba.paw.models.QuestionVotes;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.controller.utils.Commons;
import ar.edu.itba.paw.webapp.controller.utils.PaginationHeaderUtils;
import ar.edu.itba.paw.webapp.dto.input.VoteDto;
import ar.edu.itba.paw.webapp.dto.output.QuestionVoteDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Path("/questions/{questionId}/votes")
public class QuestionsVotesController {

    @Autowired
    private QuestionService qs;

    @Autowired
    private Commons commons;

    @Context
    private UriInfo uriInfo;

    /*
     * Votes
     */
    @GET
    public Response getVotesByQuestion(@PathParam("questionId") long questionId,
            @QueryParam("userId") Long userId,
            @QueryParam("page") @DefaultValue("1") int page) {

        List<QuestionVotes> qv = qs.findVotesByQuestionId(questionId, userId, page - 1);

        long pages = qs.findVotesByQuestionIdPagesCount(questionId, userId);

        List<QuestionVoteDto> qvDto = qv.stream().map(x -> (QuestionVoteDto.questionVotesToQuestionVoteDto(x, uriInfo)))
                .collect(Collectors.toList());

        Response.ResponseBuilder res = Response.ok(
                new GenericEntity<List<QuestionVoteDto>>(qvDto) {
                });

        return PaginationHeaderUtils.addPaginationLinks(page, (int) pages, uriInfo.getAbsolutePathBuilder(), res,
                uriInfo.getQueryParameters());

    }

    @GET
    @Path("/{userId}")
    public Response getVote(@PathParam("questionId") long questionId, @PathParam("userId") long userId) {
        QuestionVotes qv = qs.getQuestionVote(questionId, userId);
        return Response
                .ok(new GenericEntity<QuestionVoteDto>(QuestionVoteDto.questionVotesToQuestionVoteDto(qv, uriInfo)) {
                }).build();
    }

    @PUT
    @Path("/{userId}")
    @Consumes(value = { MediaType.APPLICATION_JSON })
    public Response updateVote(@PathParam("questionId") long questionId, @PathParam("userId") long userId,
            @RequestBody @NotNull(message = "body.cannot.be.empty") @Valid VoteDto voteDto) {

        User user = commons.currentUser();

        if (!qs.questionVote(questionId, voteDto.getVote(), user))
            return Response.notModified().build();

        return Response.ok().build();
    }

    @DELETE
    @Path("/{userId}")
    @Consumes(value = { MediaType.APPLICATION_JSON })
    public Response updateVote(@PathParam("questionId") long id, @PathParam("userId") long userId) {

        User user = commons.currentUser();
        qs.questionVote(id, null, user);
        return Response.noContent().build();
    }
}
