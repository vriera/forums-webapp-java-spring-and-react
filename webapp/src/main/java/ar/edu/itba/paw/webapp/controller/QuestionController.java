package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.dto.input.VoteDto;
import ar.edu.itba.paw.webapp.dto.output.QuestionVoteDto;
import ar.edu.itba.paw.webapp.controller.utils.GenericResponses;
import ar.edu.itba.paw.webapp.dto.output.QuestionDto;
import ar.edu.itba.paw.webapp.controller.utils.PaginationHeaderUtils;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Path("questions")
public class QuestionController {

    @Autowired
    private SearchService ss;

    @Autowired
    private QuestionService qs;

    @Autowired
    private UserService us;

    @Autowired
    private Commons commons;

    @Context
    private UriInfo uriInfo;

    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionController.class);

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response searchQuestions(
            @DefaultValue("1") @QueryParam("page") int page,
            //filtros de search normal
            @QueryParam("query") String query,
            @QueryParam("filter") SearchFilter filter,
            @QueryParam("order") SearchOrder order,
            @QueryParam("communityId") Long communityId,
            @QueryParam("userId") Long userId,
            //filtros de mi pregunta
            @QueryParam("ownerId") Long ownerId
    ) {

        List<Question> questionList = ss.searchQuestion(query, filter , order, communityId, userId,  ownerId , page - 1);

        long pages = ss.searchQuestionPagesCount(query, filter, order, communityId, userId ,  ownerId);

        List<QuestionDto> qlDto = questionList.stream().map(x -> QuestionDto.questionToQuestionDto(x, uriInfo))
                .collect(Collectors.toList());

        if (qlDto.isEmpty())
            return Response.noContent().build();
        Response.ResponseBuilder res = Response.ok(new GenericEntity<List<QuestionDto>>(qlDto) {
        });

        return PaginationHeaderUtils.addPaginationLinks(page, (int) pages, uriInfo.getAbsolutePathBuilder(), res,
                uriInfo.getQueryParameters());
    }

    @POST
    @Consumes(value = { MediaType.MULTIPART_FORM_DATA })
    public Response create(
            @Valid @NotEmpty(message = "NotEmpty.questionForm.title") @FormDataParam("title") final String title,
            @Valid @NotEmpty(message = "NotEmpty.questionForm.body") @FormDataParam("body") final String body,
            @Valid @NotEmpty(message = "NotEmpty.questionForm.community") @FormDataParam("community") final String communityId,
            @Valid @NotEmpty @FormDataParam("file") FormDataBodyPart file) {
        User u = commons.currentUser();

        // TODO: MAP ERRORS!
        byte[] image = null;
        try {
            image = IOUtils.toByteArray(((BodyPartEntity) file.getEntity()).getInputStream());
        } catch (IOException e) {
            return GenericResponses.serverError("image.read.error", "Unknown error while reading image");
        }

        Question question = null;
        try {

            question = qs.create(title, body, u, Long.parseLong(communityId), image);
        } catch (Exception e) {
            return GenericResponses.conflict("question.not.created", null);
        }

        if (question == null)
            throw new BadRequestException();

        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(question.getId())).build();
        return Response.created(uri).build();
    }

    @GET
    @Path("/{id}/")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response getQuestion(@PathParam("id") final long id) {

        final Question question = qs.findById(id);

        QuestionDto questionDto = QuestionDto.questionToQuestionDto(question, uriInfo);

        LOGGER.info(questionDto.getTitle());
        return Response.ok(new GenericEntity<QuestionDto>(questionDto) {
        })
                .build();
    }


    /*
     * Votes
     */
    @GET
    @Path("/{id}/votes")
    public Response getVotesByQuestion(@PathParam("id") long questionId, @QueryParam("userId") Long userId,
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
    @Path("/{id}/votes/users/{userId}")
    public Response getVote(@PathParam("id") long questionId, @PathParam("userId") long userId) {
        QuestionVotes qv = qs.getQuestionVote(questionId, userId);
        return Response
                .ok(new GenericEntity<QuestionVoteDto>(QuestionVoteDto.questionVotesToQuestionVoteDto(qv, uriInfo)) {
                }).build();
    }

    @PUT
    @Path("/{id}/votes")
    @Consumes(value = { MediaType.APPLICATION_JSON })
    public Response updateVote(@PathParam("id") long id, @PathParam("userId") long userId,
                               @RequestBody @NotNull(message = "body.cannot.be.empty") @Valid VoteDto voteDto) {

        User user = commons.currentUser();

        Question question = qs.findById(id);

        if (qs.questionVote(question, voteDto.getVote(), user))
            return Response.notModified().build();

        return GenericResponses.badRequest();
    }

    @DELETE
    @Path("/{id}/votes")
    @Consumes(value = { MediaType.APPLICATION_JSON })
    public Response updateVote(@PathParam("id") long id, @PathParam("userId") long userId) {

        User user = commons.currentUser();

        qs.questionVote(questionId, null, user);
        return Response.noContent().build();
    }
}
