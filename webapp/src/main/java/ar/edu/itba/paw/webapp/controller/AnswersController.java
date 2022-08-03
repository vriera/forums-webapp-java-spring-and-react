package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.AnswersService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Answer;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.webapp.controller.utils.AuthenticationUtils;
import ar.edu.itba.paw.webapp.dto.AnswerDto;
import ar.edu.itba.paw.webapp.dto.QuestionDto;
import ar.edu.itba.paw.webapp.form.AnswersForm;
import ar.edu.itba.paw.webapp.form.QuestionForm;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.Optional;

@Component
@Path("answers")
public class AnswersController {
    @Autowired
    private AnswersService as;

    @Autowired
    private UserService us;

    @Autowired
    private Commons commons;

    @Context
    private UriInfo uriInfo;


    @GET
    @Path("/{id}/")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response getAnswer(@QueryParam("page") @DefaultValue("1") int page, @PathParam("id") final Long id, @DefaultValue("5") @QueryParam("limit") final Integer limit) {
        final AnswerDto answer = AnswerDto.answerToAnswerDto(as.findById(id).get() ,uriInfo); //TODO chequear que exista y que el user tenga permiso
        return Response.ok(new GenericEntity<AnswerDto>(answer){})
                .build();
    }
/*
    @PUT
    @Path("/{id}/verify/")
    public Response verifyAnswer(@PathVariable("id") long id, @RequestParam("verify") boolean verify){

        Optional<Answer> answer = as.verify(id, verify);
        return Response.ok().build();

    }

 */


    @PUT
    @Path("/{id}/vote/user/{idUser}")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response updateVote (@PathParam("id") Long id,@PathParam("idUser") Long idUser, @RequestParam("vote") Boolean vote) {
        Optional<Answer> answer = as.answerVote(id,vote,us.findById(id).get().getEmail());
        return Response.ok().build();
    }




    @POST
    @Path("/{id}/")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response create(@PathParam("id") final Long id,@Valid final AnswersForm form) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Answer> answer = as.create(form.getBody(), email, id);
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(answer.get().getId())).build();
        return Response.created(uri).build();
    }


/*
    @DELETE
    @Path("/{id}/")
    public Response deleteAnswer(@PathVariable("id") long id){
        Long idQuestion = as.findById(id).get().getQuestion().getId();
        as.deleteAnswer(id);
        return Response.ok().build();
    }

 */



}

