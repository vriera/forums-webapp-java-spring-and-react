package ar.edu.itba.paw.webapp.controller;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.controller.utils.GenericResponses;
import ar.edu.itba.paw.webapp.controller.dto.DashboardQuestionListDto;
import ar.edu.itba.paw.webapp.controller.dto.QuestionDto;
import ar.edu.itba.paw.webapp.controller.dto.QuestionSearchDto;
import org.apache.commons.io.IOUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;


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


	//Information user
	@GET
	@Path("/user")
	@Produces(value = {MediaType.APPLICATION_JSON})
	public Response userQuestions(@DefaultValue("0") @QueryParam("page") final int page) {

		User u = commons.currentUser();
		if (u == null) {
			//TODO mejores errores
			return GenericResponses.notAuthorized();
		}

		List<Question> ql = us.getQuestions(u.getId(), page);
		DashboardQuestionListDto qlDto = DashboardQuestionListDto.questionListToQuestionListDto(ql, uriInfo, page, 5, us.getPageAmountForQuestions(u.getId()));

		return Response.ok(
				new GenericEntity<DashboardQuestionListDto>(qlDto) {
				}
		).build();
	}//TODO: ESTO HAY QUE CAMBIARLO ESTA MAL /USER (SI NO AGREGAR EL ID CORRESPONDIENTE)

	@GET
	@Path("/{id}/")
	@Produces(value = {MediaType.APPLICATION_JSON,})
	public Response getQuestion(@PathParam("id") final Long id) {
		final Optional<User> user = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
		final Optional<Question> question;
		if (!user.isPresent()) question = qs.findById(null, id);
		else question =  qs.findById(user.get(), id);
		if (!question.isPresent()) {
			LOGGER.error("Attempting to access non-existent question: id {}", id);
			return GenericResponses.notFound();
		} else {
			if(user.isPresent() && !cs.canAccess(user.get(), question.get().getCommunity())){
				LOGGER.error("Attempting to access to a question that the user not have access: id {}", id);
				return GenericResponses.cantAccess();
			}
			QuestionDto questionDto = QuestionDto.questionDtoToQuestionDto(question.get(), uriInfo);
			LOGGER.info(questionDto.getTitle());
			return Response.ok(new GenericEntity<QuestionDto>(questionDto) {
			})
					.build();
		}
	}

	@PUT
	@Path("/{id}/vote/user/{idUser}")
	@Consumes(value = {MediaType.APPLICATION_JSON})
	public Response updateVote (@PathParam("id") Long id,@PathParam("idUser") Long idUser, @QueryParam("vote") Boolean vote) {
		final Optional<User> user = us.findById(idUser);
		if(user.isPresent()){
			Optional<Question> question = qs.findById(user.get(), id);
			if(!question.isPresent()) return GenericResponses.notFound();
			Boolean b = qs.questionVote(question.get(), vote, user.get().getEmail());
			if(!b){
				LOGGER.error("Attempting to access to a question that the user not have access: id {}", id);
				return GenericResponses.cantAccess();
			}
			return Response.ok().build();
		}
		return Response.status(Response.Status.BAD_REQUEST).build(); //ver si poner mensaje body
	}

	@DELETE
	@Path("/{id}/vote/user/{idUser}")
	@Consumes(value = {MediaType.APPLICATION_JSON})
	public Response updateVote (@PathParam("id") Long id,@PathParam("idUser") Long idUser) {
		final Optional<User> user = us.findById(idUser);
		if(user.isPresent()){
			Optional<Question> question = qs.findById(user.get(), id);
			if(!question.isPresent()) return GenericResponses.notFound();
			Boolean b = qs.questionVote(question.get(), null, user.get().getEmail());
			if(!b){
				LOGGER.error("Attempting to access to a question that the user not have access: id {}", id);
				return GenericResponses.cantAccess();
			}
			return Response.ok().build();
		}
		return Response.status(Response.Status.BAD_REQUEST).build(); //ver si poner mensaje body
	}





	@POST
	@Path("")
	@Consumes(value = {MediaType.MULTIPART_FORM_DATA})
	public Response create(@FormDataParam("title") final String title,@FormDataParam("body") final String body,@FormDataParam("community") final String community,  @FormDataParam("file") FormDataBodyPart file ) {
		byte[] image = null;
		try {
			image = IOUtils.toByteArray(((BodyPartEntity) file.getEntity()).getInputStream());
		} catch (IOException e) {
			//todo
		}
		//todo revisar errores
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		Optional<Question> question;
		try {
			question = qs.create(title, body, email, Integer.parseInt(community), image);
		} catch (Exception e) {
			LOGGER.error("error al crear question excepción:" + e.getMessage());
			return GenericResponses.badRequest();
		}
		if (question.isPresent()) {
			final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(question.get().getId())).build();
			return Response.created(uri).build();
		} else return GenericResponses.notFound(); //TODO: VER MEJOR ERROR


	}






}






