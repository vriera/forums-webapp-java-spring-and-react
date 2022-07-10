package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.AnswersService;
import ar.edu.itba.paw.webapp.dto.AnswerDto;
import ar.edu.itba.paw.webapp.dto.QuestionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Path("answers")
public class AnswersController {
    @Autowired
    private AnswersService as;

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



}
