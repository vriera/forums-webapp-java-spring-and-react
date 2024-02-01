package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.exceptions.BadParamsException;
import ar.edu.itba.paw.interfaces.exceptions.GenericBadRequestException;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.controller.utils.GenericResponses;
import ar.edu.itba.paw.webapp.controller.dto.QuestionDto;

import ar.edu.itba.paw.webapp.controller.utils.PaginationHeaderUtils;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
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
    private AnswersService as;

    @Autowired
    private SearchService ss;
    @Autowired
    private CommunityService cs;

    @Autowired
    private ForumService fs;

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
    @Path("") //TODO: pasar esto a SPRING SECURITY
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response searchQuestions(
            @DefaultValue("") @QueryParam("query") String query,
            @DefaultValue("0") @QueryParam("filter") int filter,
            @DefaultValue("0") @QueryParam("order") int order,
            @DefaultValue("1") @QueryParam("page") int page,
            @DefaultValue("10") @QueryParam("limit") int limit,
            @DefaultValue("-1") @QueryParam("communityId") long communityId,
            @DefaultValue("-1") @QueryParam("userId") Long userId ) throws BadParamsException {

        User u = commons.currentUser();
        List<Question> questionList = ss.search(query, SearchFilter.values()[filter], SearchOrder.values()[order], communityId, userId,u, limit, page);
        int questionCount = ss.countQuestionQuery(query, SearchFilter.values()[filter], SearchOrder.values()[order], communityId, u, userId);
        int pages = (int) Math.ceil((double) questionCount / limit);
        List<QuestionDto> qlDto = questionList.stream().map(x -> QuestionDto.questionDtoToQuestionDto(x , uriInfo) ).collect(Collectors.toList());
        if(qlDto.isEmpty())  return Response.noContent().build();
        Response.ResponseBuilder res = Response.ok(new GenericEntity<List<QuestionDto>>(qlDto) {});
        return PaginationHeaderUtils.addPaginationLinks(page , pages,uriInfo.getAbsolutePathBuilder() , res);
    }

    @GET
    @Path("/{id}/")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response getQuestion(@PathParam("id") final Long id) {
        final Optional<User> user = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        final Optional<Question> question;
        if (!user.isPresent()) question = qs.findById(null, id); else question = qs.findById(user.get(), id);
        if (!question.isPresent()) {
            LOGGER.error("Attempting to access non-existent question: id {}", id);
            return GenericResponses.notFound();
        }
        QuestionDto questionDto = QuestionDto.questionDtoToQuestionDto(question.get(), uriInfo);
        LOGGER.info(questionDto.getTitle());
        return Response.ok(new GenericEntity<QuestionDto>(questionDto) {
            })
                    .build();
    }

    @PUT
    @Path("/{id}/votes")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response updateVote(@PathParam("id") Long id, @QueryParam("vote") Boolean vote) {
        User user = commons.currentUser();
        Optional<Question> question = qs.findById(user, id);
        if (!question.isPresent()) return GenericResponses.notFound();
        qs.questionVote(question.get(), vote, user.getEmail());
        return Response.noContent().build();

    }

    @DELETE
    @Path("/{id}/votes")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response updateVote(@PathParam("id") Long id) {
        User user = commons.currentUser();
        Optional<Question> question = qs.findById(user, id);
        if (!question.isPresent()) return GenericResponses.notFound();
        qs.questionVote(question.get(), null, user.getEmail());
        return Response.noContent().build();
    }


    @POST
    @Path("") //TODO: pasar esto a SPRING SECURITY
    @Consumes(value = {MediaType.MULTIPART_FORM_DATA})
    @PreAuthorize("@accessControl.checkCanAccessToCommunity(authentication,Integer.parseInt( #community))")
    public Response create(@FormDataParam("title") final String title, @FormDataParam("body") final String body, @FormDataParam("community") final String community, @FormDataParam("file") FormDataBodyPart file) throws GenericBadRequestException {
        User u = commons.currentUser();
        byte[] image = null;
        if (file != null) {
            try {
                image = IOUtils.toByteArray(((BodyPartEntity) file.getEntity()).getInputStream());
            } catch (IOException e) {
                return GenericResponses.serverError("image.read.error", "Unknown error while reading image");
            }
        }


        Optional<Question> question = qs.create(title, body, u, Long.valueOf(community), image);
        if (question.isPresent()) {
            final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(question.get().getId())).build();
            return Response.created(uri).build();
        } else return GenericResponses.badRequest("question.not.created", null);


    }

}






