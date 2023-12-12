package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.interfaces.services.AnswersService;
import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.models.AnswerVotes;
import ar.edu.itba.paw.webapp.controller.utils.Commons;
import ar.edu.itba.paw.webapp.controller.utils.PaginationHeaderUtils;
import ar.edu.itba.paw.webapp.dto.input.VoteDto;
import ar.edu.itba.paw.webapp.dto.output.AnswerVoteDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.ServletContext;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Path("/answers/{answerId}/votes")
public class AnswersVotesController {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionController.class);
    @Autowired
    private AnswersService as;

    @Autowired
    private SearchService ss;

    @Autowired
    private Commons commons;

    @Context
    private UriInfo uriInfo;

    @Autowired
    private ServletContext servletContext;

    @GET
    public Response getVotesByAnswer(@PathParam("answerId") long answerId,
                                     @QueryParam("userId") Long userId,
                                     @QueryParam("page") @DefaultValue("1") int page) {
        List<AnswerVotes> answerVotes = as.findVotesByAnswerId(answerId, userId, page - 1);
        long pages = as.findVotesByAnswerIdPagesCount(answerId, userId);

        List<AnswerVoteDto> avDto = answerVotes.stream().map(x -> (AnswerVoteDto.AnswerVotesToAnswerVoteDto(x, uriInfo)))
                .collect(Collectors.toList());

        Response.ResponseBuilder res = Response.ok(
                new GenericEntity<List<AnswerVoteDto>>(avDto) {
                });

        return PaginationHeaderUtils.addPaginationLinks(page, (int) pages, uriInfo.getAbsolutePathBuilder(), res,
                uriInfo.getQueryParameters());
    }


    @GET
    @Path("/{userId}")
    public Response getVote(@PathParam("answerId") long answerId, @PathParam("userId") long userId) {

        AnswerVotes av = as.getAnswerVote(answerId, userId);
        return Response.ok(new GenericEntity<AnswerVoteDto>(AnswerVoteDto.AnswerVotesToAnswerVoteDto(av, uriInfo)) {
        }).build();
    }

    @PUT
    @Path("/{userId}")
    @Consumes(value = { MediaType.APPLICATION_JSON })
    public Response updateVote(@PathParam("answerId") long answerId, @PathParam("userId") long userId,
                               @RequestBody @NotNull(message = "body.cannot.be.empty") @Valid VoteDto voteDto) {


        if(!as.answerVote(answerId, voteDto.getVote(),userId))
            return Response.notModified().build();

        return Response.noContent().build();
    }

    @DELETE
    @Path("/{userId}")
    @Consumes(value = { MediaType.APPLICATION_JSON })
    public Response deleteVote(@PathParam("answerId") Long answerId, @PathParam("userId") long userId) {

        as.answerVote(answerId, null, userId);

        return Response.noContent().build();
    }

}
