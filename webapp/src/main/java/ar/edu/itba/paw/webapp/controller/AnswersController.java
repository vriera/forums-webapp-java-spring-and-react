package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.AnswersService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Answer;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;
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
    private Commons commons;

    @Context
    private UriInfo uriInfo;

    @Autowired
    private ServletContext servletContext;



    @GET
    @Path("/{id}/")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response getAnswer(@QueryParam("page") @DefaultValue("1") int page, @PathParam("id") final Long id, @DefaultValue("5") @QueryParam("limit") final Integer limit) {
        final AnswerDto answer = AnswerDto.answerToAnswerDto(as.findById(id).get() ,uriInfo); //TODO chequear que exista y que el user tenga permiso
        return Response.ok(new GenericEntity<AnswerDto>(answer){})
                .build();
    }

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response getAnswers(@QueryParam("page") @DefaultValue("1") int page,@DefaultValue("5") @QueryParam("limit") final Integer limit,@QueryParam("idQuestion") final Long idQuestion) {
        final User user = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).get(); //TODO chequear que el user funque bien
        List<AnswerDto> answers = null;
        if(idQuestion==null){
            answers = as.getAnswers(limit,page,user).stream().map(a-> AnswerDto.answerToAnswerDto(a,uriInfo)).collect(Collectors.toList());;
        }else{
            answers = as.findByQuestion(idQuestion,limit,page,user).stream().map(a-> AnswerDto.answerToAnswerDto(a,uriInfo)).collect(Collectors.toList()); //TODO chequear que exista y que el user tenga permiso
        }

        return Response.ok(new GenericEntity<List<AnswerDto>>(answers) {
        })
                .build();
    }


    @POST
    @Path("/{id}/verify/")
    public Response verifyAnswer(@PathParam("id") long id){

        Optional<Answer> answer = as.verify(id, true);
        return Response.ok().build();

    }

    @DELETE
    @Path("/{id}/verify/")
    public Response unVerifyAnswer(@PathParam("id") long id){

        Optional<Answer> answer = as.verify(id, false);
        return Response.ok().build();

    }



    @PUT
    @Path("/{id}/vote/user/{idUser}")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response updateVote (@PathParam("id") Long id,@PathParam("idUser") Long idUser, @QueryParam("vote") Boolean vote) {
        Optional<Answer> answer = as.answerVote(id,vote,us.findById(id).get().getEmail());
        return Response.ok().build();
    }

    @DELETE
    @Path("/{id}/vote/user/{idUser}")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response updateVote (@PathParam("id") Long id,@PathParam("idUser") Long idUser) {
        Optional<Answer> answer = as.answerVote(id,null,us.findById(id).get().getEmail());
        return Response.ok().build();
    }




    @POST
    @Path("/{id}/")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response create(@PathParam("id") final Long id,@Valid final AnswersForm form) {
        //String email = SecurityContextHolder.getContext().getAuthentication().getName();
        final String baseUrl = uriInfo.getBaseUriBuilder().replacePath(servletContext.getContextPath()).toString();
        //ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString()
        Optional<Answer> answer = as.create(form.getBody(), "natu2000@gmail.com", id,baseUrl);
        final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(answer.get().getId())).build();
        return Response.created(uri).build();
    }


    @DELETE
    @Path("/{id}/")
    public Response deleteAnswer(@PathParam("id") long id){
        Long idQuestion = as.findById(id).get().getQuestion().getId();
        as.deleteAnswer(id);
        return Response.ok().build();
    }




}

