package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.dto.output.QuestionVoteDto;
import ar.edu.itba.paw.webapp.controller.utils.GenericResponses;
import ar.edu.itba.paw.webapp.dto.output.QuestionDto;

import ar.edu.itba.paw.webapp.controller.utils.PaginationHeaderUtils;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.stereotype.Component;


import javax.validation.Valid;
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
    private final static int PAGE_SIZE = 10;

    @Autowired
    private SearchService ss;

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
            @DefaultValue("-1") @QueryParam("communityId") int communityId,
            @DefaultValue("-1") @QueryParam("userId") Integer userId
    ) {
        //El no such element va a tirarlo el security?
        //@ModelAttribute("paginationForm") PaginationForm paginationForm)
        int size = PAGE_SIZE;
        int offset = (page -1) *size ;
        int limit = size;

        User u = commons.currentUser();

        List<Question> questionList = ss.search(query, SearchFilter.values()[filter], SearchOrder.values()[order], communityId, u, limit, offset);

        int questionCount = ss.countQuestionQuery(query, SearchFilter.values()[filter], SearchOrder.values()[order], communityId, u);

        int pages = (int) Math.ceil((double) questionCount / size);

        List<QuestionDto> qlDto = questionList.stream().map(x -> QuestionDto.questionToQuestionDto(x , uriInfo) ).collect(Collectors.toList());

        if(qlDto.isEmpty())
            return Response.noContent().build();
        Response.ResponseBuilder res = Response.ok(new GenericEntity<List<QuestionDto>>(qlDto) {});
        UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
        if(!query.isEmpty())
            uriBuilder.queryParam("query" , query);
        if(filter != 0)
            uriBuilder.queryParam("filter" , filter);
        if(order != 0)
            uriBuilder.queryParam("order" , order);
        if(communityId != -1)
            uriBuilder.queryParam("communityId" , communityId);
        if(userId != -1)
            uriBuilder.queryParam("requesterId" , userId);

        return PaginationHeaderUtils.addPaginationLinks(page , pages,uriBuilder  , res);
    }


    @GET
    @Path("/{id}/")
    @Produces(value = {MediaType.APPLICATION_JSON,})
    public Response getQuestion(@PathParam("id") final Long id) {

        final Question question = qs.findById(id);


        QuestionDto questionDto = QuestionDto.questionToQuestionDto(question, uriInfo);

        LOGGER.info(questionDto.getTitle());
        return Response.ok(new GenericEntity<QuestionDto>(questionDto) {
            })
                    .build();
    }

    @GET
    @Path("/{id}/votes")
    public Response getVotesByQuestion(@PathParam("id") Long questionId, @QueryParam("userId") Long userId , @QueryParam("page") @DefaultValue("1") int page) {
        //el no such element lo va a tirar el security
        List<QuestionVotes> qv = qs.findVotesByQuestionId(questionId,userId,page -1);

        int pages = qs.findVotesByQuestionIdCount(questionId,userId);

        List<QuestionVoteDto> qvDto = qv.stream().map( x->(QuestionVoteDto.questionVotesToQuestionVoteDto(x , uriInfo) )).collect(Collectors.toList());

        Response.ResponseBuilder res = Response.ok(
                new GenericEntity<List<QuestionVoteDto>>(qvDto) {
                });


        UriBuilder uri = uriInfo.getAbsolutePathBuilder();

        if(userId != null && userId > 0)
            uri.queryParam("userId", userId);

        return PaginationHeaderUtils.addPaginationLinks(page, pages, uri, res);

    }


    @GET
    @Path("/{id}/votes/users/{userId}")
    public Response getVote(@PathParam("id") Long questionId, @PathParam("userId") Long userId) {
        QuestionVotes qv = qs.getQuestionVote(questionId,userId);
        return Response.ok(new GenericEntity<QuestionVoteDto>(QuestionVoteDto.questionVotesToQuestionVoteDto(qv , uriInfo)) {
        }).build();
    }

    //TODO: Pasar a body
    @PUT
    @Path("/{id}/votes/users/{userId}")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response updateVote(@PathParam("id") Long id, @PathParam("userId") Long userId, @QueryParam("vote") Boolean vote) {

        User user = us.findById(userId);

       Question question = qs.findById(id);

        if( qs.questionVote(question, vote, user))
            return Response.noContent().build();

        return GenericResponses.badRequest();

    }

    @DELETE
    @Path("/{id}/votes/users/{userId}")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response updateVote(@PathParam("id") Long id, @PathParam("userId") Long userId) {

        User user = us.findById(userId);
        final Question question = qs.findById(id);
        qs.questionVote(question, null, user);
        return Response.noContent().build();
    }


    @POST
    @Path("") //TODO: pasar esto a SPRING SECURITY
    @Consumes(value = {MediaType.MULTIPART_FORM_DATA})
    public Response create(@Valid @NotEmpty(message = "NotEmpty.questionForm.title")  @FormDataParam("title") final String title,
                           @Valid @NotEmpty(message = "NotEmpty.questionForm.body") @FormDataParam("body") final String body,
                           @Valid @NotEmpty(message = "NotEmpty.questionForm.community") @FormDataParam("community") final String community,
                           @Valid @NotEmpty @FormDataParam("file") FormDataBodyPart file) {
        User u = commons.currentUser();
      /*  if (u == null) {
            return GenericResponses.notAuthorized();
        }*/ //TODO REVISAR
        byte[] image = null;
        if (file != null) {
            try {
                image = IOUtils.toByteArray(((BodyPartEntity) file.getEntity()).getInputStream());
            } catch (IOException e) {
                return GenericResponses.serverError("image.read.error", "Unknown error while reading image");
            }
        }

        Question question = null;
        try {

            Optional<Forum> f = fs.findByCommunity(Integer.parseInt(community)).stream().findFirst();
            if (!f.isPresent()) {
                return GenericResponses.badRequest("forum.not.found", "A forum for the given community has not been found");
            }

            question = qs.create(title, body, u, f.get(), image);
        } catch (Exception e) {
            return GenericResponses.conflict("question.not.created", null);
        }
        if( question == null)
            throw new BadRequestException();

            final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(question.getId())).build();
            return Response.created(uri).build();
    }



    @GET
    @Path("/owned") //TODO: pasar esto a SPRING SECURITY
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response ownedQuestions(
            @DefaultValue("1") @QueryParam("page") int page,
            @DefaultValue("-1") @QueryParam("userId") Integer userId
    ) {
        User u = commons.currentUser();
        if(  u == null )
            return GenericResponses.notAuthorized();
        if(u.getId() != userId )
            return GenericResponses.cantAccess();
        if(userId < -1)
            return GenericResponses.badRequest();

        List<Question> questionList = us.getQuestions(userId , page - 1);
        LOGGER.debug("Questions owned by user {} : {}" , userId , questionList.size());
        int pages = us.getPageAmountForQuestions(userId);

//        int pages = (int) Math.ceil((double) count / size);

        List<QuestionDto> qlDto = questionList.stream().map(x -> QuestionDto.questionToQuestionDto(x , uriInfo) ).collect(Collectors.toList());

        if(qlDto.isEmpty())
            return Response.noContent().build();
        Response.ResponseBuilder res = Response.ok(new GenericEntity<List<QuestionDto>>(qlDto) {});
        UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();

        if(userId != -1)
            uriBuilder.queryParam("userId" , userId);
        return PaginationHeaderUtils.addPaginationLinks(page , pages,uriBuilder  , res);
    }

}






