package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.exceptions.BadParamsException;
import ar.edu.itba.paw.interfaces.exceptions.GenericNotFoundException;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.Answer;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.controller.dto.AnswerDto;
import ar.edu.itba.paw.webapp.controller.utils.GenericResponses;
import ar.edu.itba.paw.webapp.controller.utils.PaginationHeaderUtils;
import ar.edu.itba.paw.webapp.form.AnswersForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Path("answers")
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

    @Autowired
    private CommunityService cs;

    @GET
    @Path("/{id}/")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response getAnswer(@PathParam("id") final Long id) {
        final Optional<Answer> answer = as.findById(id);
        if (answer.isPresent()) {
            final AnswerDto answerDto = AnswerDto.answerToAnswerDto(answer.get(), uriInfo);
            return Response.ok(new GenericEntity<AnswerDto>(answerDto) {
            })
                    .build();

        } else
            return GenericResponses.notFound();
    }

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response getAnswers(@QueryParam("page") @DefaultValue("1") int page,
            @DefaultValue("5") @QueryParam("limit") final Integer limit,
            @QueryParam("idQuestion") final Long idQuestion, @QueryParam("userId") final Long userId) throws BadParamsException, GenericNotFoundException { //TODO: AGREGAR SSECURITY USER = USER;
        List<AnswerDto> answers = null;
        Optional<Long> countAnswers;
        answers = as.getAnswers(limit, page, userId, idQuestion).stream()
                    .map(a -> AnswerDto.answerToAnswerDto(a, uriInfo)).collect(Collectors.toList());
        countAnswers = as.countAnswers(idQuestion,userId);
        if (answers.isEmpty())
            return Response.noContent().build();

        Response.ResponseBuilder responseBuilder = Response.ok(new GenericEntity<List<AnswerDto>>(answers) {
        });
        UriBuilder uri = uriInfo.getAbsolutePathBuilder();
        uri.queryParam("limit", limit);
        if(idQuestion != null) uri.queryParam("idQuestion", idQuestion);
        else  uri.queryParam("userId", userId);

        return PaginationHeaderUtils.addPaginationLinks(page, countAnswers.get().intValue(), uri,
                responseBuilder);

    }

    @POST
    @Consumes(value = { MediaType.APPLICATION_JSON })
    @PreAuthorize("@accessControl.checkCanAccessToQuestionAnswerForm(authentication, #form)")
    public Response create(@Valid final AnswersForm form) throws GenericNotFoundException, BadParamsException { //todo: agregar spring secutiry user
        final User user = commons.currentUser();
        final String baseUrl = uriInfo.getBaseUriBuilder().replacePath(servletContext.getContextPath()).toString();
        Optional<Answer> answer = as.create(form.getBody(), user, form.getQuestionId(), baseUrl);
        if (!answer.isPresent()) return GenericResponses.badRequest();
        final URI uri = uriInfo.getBaseUriBuilder().path("answers").path(String.valueOf(answer.get().getId())).build();
        return Response.created(uri).build();
    }

    @POST
    @Path("/{id}/verification/")
    public Response verifyAnswer(@PathParam("id") long id) throws GenericNotFoundException {

            as.verify(id, true);
            return Response.noContent().build();

    }

    @DELETE
    @Path("/{id}/verification/")
    public Response unVerifyAnswer(@PathParam("id") long id) throws GenericNotFoundException {
        as.verify(id, false);
        return Response.noContent().build();
    }

    @PUT
    @Path("/{id}/votes")
    @Consumes(value = { MediaType.APPLICATION_JSON })
    public Response updateVote(@PathParam("id") long id, @QueryParam("vote") Boolean vote) throws BadParamsException, GenericNotFoundException {
        if(vote == null) return Response.status(Response.Status.BAD_REQUEST).build();
        final User user = commons.currentUser();
        as.answerVote(id, user, vote);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{id}/votes")
    @Consumes(value = { MediaType.APPLICATION_JSON })
    public Response deleteVote(@PathParam("id") long id) throws BadParamsException, GenericNotFoundException {
        final User user = commons.currentUser();
        as.answerVote(id, user, null);
        return Response.noContent().build();
    }



    /*
     * @DELETE
     * 
     * @Path("/{id}/")
     * public Response deleteAnswer(@PathParam("id") long id) {
     * Long idQuestion = as.findById(id).get().getQuestion().getId();
     * as.deleteAnswer(id);
     * return Response.ok().build();
     * }
     */

/*    @GET
    @Path("/owner")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response userAnswers(@DefaultValue("1") @QueryParam("page") int page,
            @DefaultValue("-1") @QueryParam("requestorId") int userId) {
        User u = commons.currentUser();
        List<Answer> al = us.getAnswers(u.getId(), page - 1);
        if (al.isEmpty())
            return Response.noContent().build();

        int pages = us.getPageAmountForAnswers(u.getId());
        List<AnswerDto> alDto = al.stream().map(x -> AnswerDto.answerToAnswerDto(x, uriInfo))
                .collect(Collectors.toList());

        Response.ResponseBuilder res = Response.ok(
                new GenericEntity<List<AnswerDto>>(alDto) {
                });

        UriBuilder uri = uriInfo.getAbsolutePathBuilder();
        if (userId != -1)
            uri.queryParam("requestorId", userId);
        return PaginationHeaderUtils.addPaginationLinks(page, pages, uri, res);

    }

    @GET
    @Path("/top")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response topAnswers(
            @DefaultValue("-1") @QueryParam("requestorId") Integer userId) {
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
    }*/

}
