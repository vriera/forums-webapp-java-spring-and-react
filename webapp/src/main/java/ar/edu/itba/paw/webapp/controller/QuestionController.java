package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.controller.utils.AuthenticationUtils;
import ar.edu.itba.paw.webapp.dto.AnswerDto;
import ar.edu.itba.paw.webapp.dto.QuestionDto;
import ar.edu.itba.paw.webapp.dto.UserDto;
import ar.edu.itba.paw.webapp.form.AnswersForm;
import ar.edu.itba.paw.webapp.form.CommunityForm;
import ar.edu.itba.paw.webapp.form.PaginationForm;
import ar.edu.itba.paw.webapp.form.QuestionForm;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;


import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Base64;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;


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

	@GET
	@Produces(value = {MediaType.APPLICATION_JSON,})
	public Response listQuestions(@QueryParam("page") @DefaultValue("1") int page) {
		final List<QuestionDto> questions = qs.findAll(commons.currentUser(), page).stream().map(question -> QuestionDto.questionDtoToQuestionDto(question, uriInfo)).collect(Collectors.toList());
		return Response.ok(new GenericEntity<List<QuestionDto>>(questions) {
		})
				.build();
	}


	@GET
	@Path("/{id}/")
	@Produces(value = {MediaType.APPLICATION_JSON,})
	public Response getQuestion(@PathParam("id") final Long id) {
		final Optional<Question> question = qs.findById(commons.currentUser(), id); //TODO chequear que exista
		if (!question.isPresent()) {
			LOGGER.error("Attempting to access non-existent or forbidden question: id {}", id);
			return Response.noContent().build();
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
		//us.findById(id).get().getEmail())
		Optional<Question> question = qs.questionVote(id,vote,"natu2000@gmail.com");
		return Response.ok().build();
	}

	@DELETE
	@Path("/{id}/vote/user/{idUser}")
	@Consumes(value = {MediaType.APPLICATION_JSON})
	public Response updateVote (@PathParam("id") Long id,@PathParam("idUser") Long idUser) {
		Optional<Question> question = qs.questionVote(id,null,"natu2000@gmail.com");
		return Response.ok().build();
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
		Optional<Question> question = null;
		try {
			 question = qs.create(title,body,email, Integer.parseInt(community),image);
		}catch (Exception e){
			e.getMessage();
		}

		final URI uri = uriInfo.getAbsolutePathBuilder().path(String.valueOf(question.get().getId())).build();
		return Response.created(uri).build();

	}





}





