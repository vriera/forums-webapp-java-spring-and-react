package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.controller.utils.Commons;
import ar.edu.itba.paw.webapp.dto.input.VoteDto;
import ar.edu.itba.paw.webapp.dto.output.AnswerDto;
import ar.edu.itba.paw.webapp.dto.output.AnswerVoteDto;
import ar.edu.itba.paw.webapp.controller.utils.PaginationHeaderUtils;
import ar.edu.itba.paw.webapp.dto.input.AnswerCreateDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.ServletContext;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
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
    private SearchService ss;

    @Autowired
    private Commons commons;

    @Context
    private UriInfo uriInfo;

    @Autowired
    private ServletContext servletContext;

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response getAnswers(
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("questionId") @Min(value = 1, message = "min.questionId") Long questionId,
            @QueryParam("ownerId") @Min(value = 1, message = "min.ownerId") Long ownerId) {

        long pagesCount = ss.searchAnswerPagesCount(questionId, ownerId);

        if (pagesCount == 0)
            return Response.noContent().build();

        List<AnswerDto> answers = ss.searchAnswer(questionId, ownerId, page - 1).stream()
                .map(a -> AnswerDto.answerToAnswerDto(a, uriInfo)).collect(Collectors.toList());

        Response.ResponseBuilder responseBuilder = Response.ok(new GenericEntity<List<AnswerDto>>(answers) {
        });

        return PaginationHeaderUtils.addPaginationLinks(page, (int) pagesCount, uriInfo.getAbsolutePathBuilder(),
                responseBuilder, uriInfo.getQueryParameters());
    }

    @POST
    @Consumes(value = { MediaType.APPLICATION_JSON })
    public Response create(@Valid final AnswerCreateDto form) {
        final User user = commons.currentUser();

        final String baseUrl = uriInfo.getBaseUriBuilder().replacePath(servletContext.getContextPath()).toString();

        Answer answer = as.create(form.getBody(), user, form.getQuestionId(), baseUrl);

        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(answer.getId())).build();

        return Response.created(uri).build();
    }

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

    // Verification
    @POST
    @Path("/{id}/verification/")
    public Response verifyAnswer(@PathParam("id") long id) {

        as.verify(id, true);

        return Response.noContent().build();
    }

    @DELETE
    @Path("/{id}/verification/")
    public Response unVerifyAnswer(@PathParam("id") long id) {

        as.verify(id, false);

        return Response.noContent().build();
    }


}
