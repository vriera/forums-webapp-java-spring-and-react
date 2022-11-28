package ar.edu.itba.paw.webapp.controller;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.dto.QuestionDto;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;


@Component
@Path("questions")
public class QuestionController {
	@Autowired
	private AnswersService as;

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

	@GET
	@Path("/{id}/")
	@Produces(value = {MediaType.APPLICATION_JSON,})
	public Response getQuestion(@PathParam("id") final Long id) {
		final Optional<User> user = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
		final Optional<Question> question;
		if (!user.isPresent()) question = qs.findById(null, id);
		else question =  qs.findById(user.get(), id);
		if (!question.isPresent()) {
			LOGGER.error("Attempting to access non-existent or forbidden question: id {}", id);
			return Response.status(Response.Status.BAD_REQUEST).build();
		} else {
			QuestionDto questionDto = QuestionDto.questionDtoToQuestionDto(question.get(), uriInfo);
			return Response.ok(new GenericEntity<QuestionDto>(questionDto) {
			})
					.build();
		}
	}

	@PUT
	@Path("/{id}/vote/user/{idUser}")
	@Consumes(value = {MediaType.APPLICATION_JSON})
	public Response updateVote (@PathParam("id") Long id,@PathParam("idUser") Long idUser, @QueryParam("vote") Boolean vote) {
		final Optional<User> user = us.findById(id);
		if(user.isPresent()){
			Optional<Question> question = qs.questionVote(id, vote, user.get().getEmail()); //ya se fija si tiene o no acceso a la comunidad
			if(question.isPresent()) return Response.ok().build();
		}
		return Response.status(Response.Status.BAD_REQUEST).build();
	}

	@DELETE
	@Path("/{id}/vote/user/{idUser}")
	@Consumes(value = {MediaType.APPLICATION_JSON})
	public Response updateVote (@PathParam("id") Long id,@PathParam("idUser") Long idUser) {
		final Optional<User> user = us.findById(id);
		if(user.isPresent()){
			Optional<Question> question = qs.questionVote(id, null, user.get().getEmail()); //ya se fija si tiene o no acceso a la comunidad
			if(question.isPresent()) return Response.ok().build();
		}
		return Response.status(Response.Status.BAD_REQUEST).build();
	}



	@POST
	@Consumes(value = {MediaType.MULTIPART_FORM_DATA})
	public Response create(@FormDataParam("title") final String title,@FormDataParam("body") final String body,@FormDataParam("community") final String community,  @FormDataParam("file") FormDataBodyPart file ){


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
			 question = qs.create(title,body,email, Integer.parseInt(community),image);
		}catch (Exception e){
			LOGGER.error("error al crear question excepción:" + e.getMessage() );
			return Response.status(Response.Status.BAD_REQUEST).build();
		}

		if(question.isPresent()){
			final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(question.get().getId())).build();
			return Response.created(uri).build();
		}else return Response.status(Response.Status.BAD_REQUEST).build(); //TODO: VER MEJOR ERROR



	}





}





