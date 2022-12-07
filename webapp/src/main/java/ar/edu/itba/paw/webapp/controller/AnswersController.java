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
import ar.edu.itba.paw.webapp.form.AnswersForm;
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
    public Response getAnswer(@QueryParam("page") @DefaultValue("1") int page, @PathParam("id") final Long id, @DefaultValue("5") @QueryParam("limit") final Integer limit) {
        final Optional<User> user = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if (user.isPresent()) {
            final Optional<Answer> answer = as.findById(id);
            if (answer.isPresent()) {
                final AnswerDto answerDto = AnswerDto.answerToAnswerDto(answer.get(), uriInfo); //TODO chequear que exista y que el user tenga permiso
                if (answerDto.getQuestion().getCommunity() != null && !cs.canAccess(user.get(), answerDto.getQuestion().getCommunity()))
                    return Response.status(Response.Status.FORBIDDEN).build();
                return Response.ok(new GenericEntity<AnswerDto>(answerDto) {
                })
                        .build();

            } else return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).build(); //TODO: AGREGAR ERROR CODE DE QUE NO ES USER
        }

    }

    @GET
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response getAnswers(@QueryParam("page") @DefaultValue("1") int page, @DefaultValue("5") @QueryParam("limit") final Integer limit, @QueryParam("idQuestion") final Long idQuestion) {
        final Optional<User> user = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        List<AnswerDto> answers = null;
        if (user.isPresent()) {
            if (idQuestion == null) {
                answers = as.getAnswers(limit, page, user.get()).stream().map(a -> AnswerDto.answerToAnswerDto(a, uriInfo)).collect(Collectors.toList());//PASO LAS DEL USUARIO
            } else {
                Optional<Question> question = qs.findById(user.get(), idQuestion);
                if (question.isPresent()) return Response.status(Response.Status.NOT_FOUND).build();
                answers = as.findByQuestion(idQuestion, limit, page, user.get()).stream().map(a -> AnswerDto.answerToAnswerDto(a, uriInfo)).collect(Collectors.toList());

            }

            return Response.ok(new GenericEntity<List<AnswerDto>>(answers) {
            })
                    .build();
        } else Response.status(Response.Status.FORBIDDEN).build();

        return Response.status(Response.Status.BAD_REQUEST).build();
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
        final Optional<User> user = us.findById(id);
        if (user.isPresent()) {
            Optional<Answer> answer = as.answerVote(id, vote, user.get().getEmail()); //ya se fija si tiene o no acceso a la comunidad
            if (answer.isPresent()) return Response.ok().build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @DELETE
    @Path("/{id}/vote/user/{idUser}")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response updateVote(@PathParam("id") Long id, @PathParam("idUser") Long idUser) {
        final Optional<User> user = us.findById(id);
        if (user.isPresent()) {
            Optional<Answer> answer = as.answerVote(id, null, user.get().getEmail()); //TODO ya se fija si tiene o no acceso a la comunidad, separar diferentes errores???
            if (answer.isPresent()) return Response.ok().build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }


    @POST
    @Path("/{id}/")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response create(@PathParam("id") final Long id, @Valid final AnswersForm form) {
        final Optional<User> user = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        if (user.isPresent()) {
            final String baseUrl = uriInfo.getBaseUriBuilder().replacePath(servletContext.getContextPath()).toString();
            Optional<Answer> answer = as.create(form.getBody(), user.get().getEmail(), id, baseUrl);
            final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(answer.get().getId())).build();
            return Response.created(uri).build();
        }
        return Response.status(Response.Status.FORBIDDEN).build();

    }


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

