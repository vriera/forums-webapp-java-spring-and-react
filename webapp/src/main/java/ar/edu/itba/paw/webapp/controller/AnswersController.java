package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.AnswersService;
import ar.edu.itba.paw.interfaces.services.CommunityService;
import ar.edu.itba.paw.interfaces.services.QuestionService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Answer;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.controller.utils.GenericResponses;
import ar.edu.itba.paw.webapp.controller.dto.AnswerDto;
import ar.edu.itba.paw.webapp.controller.dto.DashboardAnswerListDto;
import ar.edu.itba.paw.webapp.controller.utils.PaginationHeaderUtils;
import ar.edu.itba.paw.webapp.form.AnswersForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response getAnswer( @PathParam("id") final Long id) {
        final Optional<User> user = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        final Optional<Answer> answer = as.findById(id);
        if (answer.isPresent()) {
            final AnswerDto answerDto = AnswerDto.answerToAnswerDto(answer.get(), uriInfo);
            if (answer.get().getQuestion().getCommunity() != null && !cs.canAccess(user.get(),answer.get().getQuestion().getCommunity()))
                return GenericResponses.cantAccess();
            return Response.ok(new GenericEntity<AnswerDto>(answerDto) {
            })
                    .build();

        } else return GenericResponses.notFound();
    }


    @GET
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response getAnswers(@QueryParam("page") @DefaultValue("1") int page, @DefaultValue("5") @QueryParam("limit") final Integer limit, @QueryParam("idQuestion") final Long idQuestion) {
        final Optional<User> user = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        List<AnswerDto> answers = null;
        Optional<Long> countAnswers;
        if (idQuestion == null) GenericResponses.badRequest("ID question missing");
        if (user.isPresent()) {
            Optional<Question> question = qs.findById(user.get(), idQuestion);
            if (!question.isPresent()) return Response.status(Response.Status.NOT_FOUND).build();
            if (question.get().getCommunity() != null && !cs.canAccess(user.get(), question.get().getCommunity()))
                return GenericResponses.cantAccess();
            answers = as.findByQuestion(idQuestion, limit, page, user.get()).stream().map(a -> AnswerDto.answerToAnswerDto(a, uriInfo)).collect(Collectors.toList());
            countAnswers = as.countAnswers(question.get().getId());
        } else {
            Optional<Question> question = qs.findById(null, idQuestion);
            if (!question.isPresent()) return Response.status(Response.Status.NOT_FOUND).build();
            if (question.get().getCommunity() != null && !cs.canAccess(null, question.get().getCommunity()))
                return GenericResponses.cantAccess();
            answers = as.findByQuestion(idQuestion, limit, page, null).stream().map(a -> AnswerDto.answerToAnswerDto(a, uriInfo)).collect(Collectors.toList());
            countAnswers = as.countAnswers(question.get().getId());
        }
        if (answers == null) GenericResponses.notFound();
        Response.ResponseBuilder responseBuilder =  Response.ok(new GenericEntity<List<AnswerDto>>(answers){});
        Response response = PaginationHeaderUtils.addPaginationLinks(page,countAnswers.get().intValue(), uriInfo.getAbsolutePathBuilder(),responseBuilder);
        return response;

    }


    @POST
    @Path("/{id}/verify/")
    public Response verifyAnswer(@PathParam("id") long id) {
        final Optional<User> user = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if (user.isPresent()) {
            Optional<Answer> answer = as.findById(id);
            if (!answer.isPresent()) return Response.status(Response.Status.NOT_FOUND).build();
            if (answer.get().getQuestion().getOwner().equals(user.get())) {
                as.verify(id, true);
                return Response.ok().build();
            }
        }

        return Response.status(Response.Status.FORBIDDEN).build();


    }

    @DELETE
    @Path("/{id}/verify/")
    public Response unVerifyAnswer(@PathParam("id") long id) {

        final Optional<User> user = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if (user.isPresent()) {
            Optional<Answer> answer = as.findById(id);
            if (!answer.isPresent()) return Response.status(Response.Status.NOT_FOUND).build();
            if (answer.get().getQuestion().getOwner().equals(user.get())) {
                as.verify(id, false);
                return Response.ok().build();
            }
        }

        return Response.status(Response.Status.FORBIDDEN).build();


    }


    @PUT
    @Path("/{id}/vote/user/{idUser}")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response updateVote(@PathParam("id") Long id, @PathParam("idUser") Long idUser, @QueryParam("vote") Boolean vote) {
        final Optional<User> user = us.findById(idUser);
        if (user.isPresent()) {
            Optional<Answer> answer = as.findById(id);
            if(!answer.isPresent()) return GenericResponses.notFound();
            Boolean b = as.answerVote(answer.get(), vote, user.get().getEmail());
            if(!b){
                LOGGER.error("Attempting to access to a question that the user not have access: id {}", id);
                return GenericResponses.cantAccess();
            }

            return Response.ok().build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @DELETE
    @Path("/{id}/vote/user/{idUser}")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response updateVote(@PathParam("id") Long id, @PathParam("idUser") Long idUser) {
        final Optional<User> user = us.findById(idUser);
        if (user.isPresent()) {
            Optional<Answer> answer = as.findById(id);
            if(!answer.isPresent()) return GenericResponses.notFound();
            Boolean b = as.answerVote(answer.get(),null, user.get().getEmail());
            if(!b){
                LOGGER.error("Attempting to access to a question that the user not have access: id {}", id);
                return GenericResponses.cantAccess();
            }

            return Response.ok().build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }


    @POST
    @Path("/{id}/")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response create(@PathParam("id") final Long id, @Valid final AnswersForm form) {
        final Optional<User> user = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if (user.isPresent()) {
            Optional<Question> q = qs.findById(user.get(),id);
            if(!q.isPresent()) return GenericResponses.notFound();
            if(!cs.canAccess(user.get(), q.get().getCommunity()))return GenericResponses.cantAccess();
            final String baseUrl = uriInfo.getBaseUriBuilder().replacePath(servletContext.getContextPath()).toString();
            Optional<Answer> answer = as.create(form.getBody(), user.get().getEmail(), id, baseUrl);
            if(!answer.isPresent()) GenericResponses.badRequest();
            final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(answer.get().getId())).build();
            return Response.created(uri).build();
        }
        return Response.status(Response.Status.FORBIDDEN).build();

    }//TODO: REVISAR SI NO HAY MUCHA LOGICA EN LOS SERVICES Y ACA


   /* @DELETE
    @Path("/{id}/")
    public Response deleteAnswer(@PathParam("id") long id) {
        Long idQuestion = as.findById(id).get().getQuestion().getId();
        as.deleteAnswer(id);
        return Response.ok().build();
    }*/





    @GET
    @Path("/user")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response userAnswers(@DefaultValue("1") @QueryParam("page") int page){
        User u = commons.currentUser();
        if( u == null ){
            //TODO mejores errores
            return GenericResponses.notAuthorized();
        }

        List<Answer> al = us.getAnswers(u.getId() , page);
        DashboardAnswerListDto alDto = DashboardAnswerListDto.answerListToQuestionListDto(al , uriInfo , page , 5 ,us.getPageAmountForAnswers(u.getId()));

        return Response.ok(
                new GenericEntity<DashboardAnswerListDto>(alDto){}
        ).build();

    }// TODO: ESTE ENDPOINT ESTA MAL DEBERIA TENER DEL ID DEL USER


}

