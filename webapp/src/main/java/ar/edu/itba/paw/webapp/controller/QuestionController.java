package ar.edu.itba.paw.webapp.controller;

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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


import javax.validation.constraints.NotNull;
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
            @DefaultValue("-1") @QueryParam("communityId") int communityId,
            @DefaultValue("-1") @QueryParam("requestorId") Integer userId
    ) {
        //NO SE SI EL SIZE me puede romper el back!
        //@ModelAttribute("paginationForm") PaginationForm paginationForm)
        int size = PAGE_SIZE;
        int offset = (page -1) *size ;
        int limit = size;

        User u = commons.currentUser();
        if(  !(userId == -1) && ( u != null && u.getId() != userId ) )
            return GenericResponses.notAuthorized();

        if(userId == -1)
            u=null;
        Optional<Community> c = cs.findById(communityId);
        if(c.isPresent())
            if(!cs.canAccess(u , c.get()))
                return GenericResponses.cantAccess("cannot.access.community" , "User does not have access to community");

        List<Question> questionList = ss.search(query, SearchFilter.values()[filter], SearchOrder.values()[order], communityId, u, limit, offset);


        int questionCount = ss.countQuestionQuery(query, SearchFilter.values()[filter], SearchOrder.values()[order], communityId, u);

        int pages = (int) Math.ceil((double) questionCount / size);

        List<QuestionDto> qlDto = questionList.stream().map(x -> QuestionDto.questionDtoToQuestionDto(x , uriInfo) ).collect(Collectors.toList());
        if(qlDto.isEmpty())  return Response.noContent().build();
        Response.ResponseBuilder res = Response.ok(new GenericEntity<List<QuestionDto>>(qlDto) {});
        UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
        if(!query.equals(""))
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


/*
	@GET
	@Produces(value = {MediaType.APPLICATION_JSON,})
	public Response listQuestions(@QueryParam("page") @DefaultValue("1") int page) {
		final List<QuestionDto> questions = qs.findAll(commons.currentUser(), page).stream().map(question -> QuestionDto.questionDtoToQuestionDto(question, uriInfo)).collect(Collectors.toList());
		return Response.ok(new GenericEntity<List<QuestionDto>>(questions) {
		})
				.build();
	}
*/
//TODO: COMENTADO PORQUE NO PARECE SER NECESARIO QUE LISTE TODAS


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
    @Path("/{id}/votes/users/{idUser}")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response updateVote(@PathParam("id") Long id, @PathParam("idUser") Long idUser, @QueryParam("vote") Boolean vote) {
        User u = commons.currentUser();
        final Optional<User> user = us.findById(idUser);
        if (user.isPresent()) {
                Optional<Question> question = qs.findById(user.get(), id);
                if (!question.isPresent()) return GenericResponses.notFound();
                qs.questionVote(question.get(), vote, user.get().getEmail());
                return Response.noContent().build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build(); //ver si poner mensaje body
    }

    @DELETE
    @Path("/{id}/votes/users/{idUser}")
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response updateVote(@PathParam("id") Long id, @PathParam("idUser") Long idUser) {
        User u = commons.currentUser();

        final Optional<User> user = us.findById(idUser);
        if (user.isPresent()) {
                Optional<Question> question = qs.findById(user.get(), id);
                if (!question.isPresent()) return GenericResponses.notFound();
                qs.questionVote(question.get(), null, user.get().getEmail());
                return Response.noContent().build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build(); //ver si poner mensaje body
    }


    @POST
    @Path("") //TODO: pasar esto a SPRING SECURITY
    @Consumes(value = {MediaType.MULTIPART_FORM_DATA})
    public Response create(@FormDataParam("title") final String title, @FormDataParam("body") final String body, @FormDataParam("community") final String community, @FormDataParam("file") FormDataBodyPart file) {
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

        Optional<Question> question;
        try {

            Optional<Forum> f = fs.findByCommunity(Integer.parseInt(community)).stream().findFirst();
            ;
            if (!f.isPresent()) {
                return GenericResponses.badRequest("forum.not.found", "A forum for the given community has not been found");
            }

            question = qs.create(title, body, u, f.get(), image);
        } catch (Exception e) {
            ;
            return GenericResponses.conflict("question.not.created", null);
        }

        if (question.isPresent()) {
            final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(question.get().getId())).build();
            return Response.created(uri).build();
        } else return GenericResponses.badRequest("question.not.created", null);


    }



    @GET
    @Path("/owned") //TODO: pasar esto a SPRING SECURITY
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response ownedQuestions(
            @DefaultValue("1") @QueryParam("page") int page,
            @DefaultValue("-1") @QueryParam("requestorId") Integer userId
    ) {
        //NO SE SI EL SIZE me puede romper el back!
        //@ModelAttribute("paginationForm") PaginationForm paginationForm)
        int size = 5;
        int offset = (page -1) *size ;
        int limit = size;

        User u = commons.currentUser();
        if(  u == null )
            return GenericResponses.notAuthorized();
        if(u.getId() != userId )
            return GenericResponses.cantAccess();
        if(userId <-1)
            return GenericResponses.badRequest();

        List<Question> questionList = us.getQuestions(userId , page -1);
        int pages = us.getPageAmountForQuestions(userId);

//        int pages = (int) Math.ceil((double) count / size);

        List<QuestionDto> qlDto = questionList.stream().map(x -> QuestionDto.questionDtoToQuestionDto(x , uriInfo) ).collect(Collectors.toList());
        if(qlDto.isEmpty())   return Response.noContent().build();
        Response.ResponseBuilder res = Response.ok(new GenericEntity<List<QuestionDto>>(qlDto) {});
        UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();

        if(userId != -1)
            uriBuilder.queryParam("requestorId" , userId);
        return PaginationHeaderUtils.addPaginationLinks(page , pages,uriBuilder  , res);
    }

}






