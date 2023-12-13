package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.controller.utils.Commons;
import ar.edu.itba.paw.webapp.dto.output.QuestionDto;
import ar.edu.itba.paw.webapp.controller.utils.PaginationHeaderUtils;
import ar.edu.itba.paw.webapp.dto.validation.StringIsLong;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Path("questions")
public class QuestionController {

    @Autowired
    private SearchService ss;

    @Autowired
    private QuestionService qs;

    @Autowired
    private Commons commons;

    @Context
    private UriInfo uriInfo;

    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionController.class);

    //3 casos

    // 1 -> ownerId -> loggedUser =id
    // 2 -> userId -> loggerUser = id
            //BUTT
                //si hay communityId -> tengo que poder acceder

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response searchQuestions(
            @DefaultValue("1") @QueryParam("page") int page,
            // filtros de search normal
            @QueryParam("query") String query,
            @QueryParam("filter") SearchFilter filter,
            @QueryParam("order") SearchOrder order,
            @QueryParam("communityId") Long communityId,
            @QueryParam("userId") Long userId,
            // filtros de mi pregunta
            @QueryParam("ownerId") Long ownerId) {

        List<Question> questionList = ss.searchQuestion(query, filter, order, communityId, userId, ownerId, page - 1);

        long pages = ss.searchQuestionPagesCount(query, filter, order, communityId, userId, ownerId);

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
            @Valid @StringIsLong @NotEmpty(message = "NotEmpty.questionForm.community") @FormDataParam("communityId") final String communityId,
            @Valid @FormDataParam("file") FormDataBodyPart file) throws  IOException{
        User u = commons.currentUser();
        byte[] image = null;
        if(file != null)
         image = IOUtils.toByteArray(((BodyPartEntity) file.getEntity()).getInputStream());


        Question question = qs.create(title, body, u, Long.parseLong(communityId), image);

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
}
