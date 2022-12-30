package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Karma;
import ar.edu.itba.paw.webapp.controller.dto.KarmaDto;
import ar.edu.itba.paw.webapp.controller.utils.GenericResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import java.util.Optional;

@Path("karma")
@Component
public class KarmaController {

    @Autowired
    private UserService us;

    @Context
    private UriInfo uriInfo;

    @Autowired
    private Commons commons;

    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionController.class);

    @GET
    @Path("/{id}")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response getQuestion(@PathParam("id") final Long id) {
        final Optional<Karma> karma = us.getKarma(id);
        if(!karma.isPresent()){
            LOGGER.error("Attempting to get karma from non-existent user : id {}" , id);
            return GenericResponses.notFound();
        }
        KarmaDto karmaDto = KarmaDto.KarmaToKarmaDto(karma.get() , uriInfo);
        return Response.ok(new GenericEntity<KarmaDto>(karmaDto) {
        })
                .build();
    }


}
