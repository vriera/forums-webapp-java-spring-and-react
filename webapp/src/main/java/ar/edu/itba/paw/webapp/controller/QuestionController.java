package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.Answer;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.controller.utils.AuthenticationUtils;
import ar.edu.itba.paw.webapp.dto.AnswerDto;
import ar.edu.itba.paw.webapp.dto.QuestionDto;
import ar.edu.itba.paw.webapp.dto.UserDto;
import ar.edu.itba.paw.webapp.form.AnswersForm;
import ar.edu.itba.paw.webapp.form.PaginationForm;
import ar.edu.itba.paw.webapp.form.QuestionForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
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
	@Produces(value = { MediaType.APPLICATION_JSON, })
	public Response listQuestions(@QueryParam("page") @DefaultValue("1") int page) {
		final List<QuestionDto> questions = qs.findAll(commons.currentUser(), page).stream().map(question -> QuestionDto.questionDtoToQuestionDto(question,uriInfo)).collect(Collectors.toList());
		return Response.ok(new GenericEntity<List<QuestionDto>>(questions){})
				.build();
	}


    @GET
    @Path("/{id}/")
    @Produces(value = { MediaType.APPLICATION_JSON, })
    public Response getQuestion( @PathParam("id") final Long id) {
        final Optional<Question> question =qs.findById(commons.currentUser(), id); //TODO chequear que exista
		if(!question.isPresent()){
			LOGGER.error("Attempting to access non-existent or forbidden question: id {}", id);
			return Response.noContent().build();
		}else {
			QuestionDto questionDto = QuestionDto.questionDtoToQuestionDto(question.get(),uriInfo);
			return Response.ok(new GenericEntity<QuestionDto>(questionDto) {
			})
					.build();
		}
    }





}
/*


	private static final Logger LOGGER = LoggerFactory.getLogger(QuestionController.class);


	@RequestMapping(path = "/question/{id}/answer" , method = RequestMethod.POST)
	public ModelAndView createAnswerPost(@ModelAttribute("answersForm") @Valid AnswersForm answersForm,@ModelAttribute("paginationForm") PaginationForm paginationForm,BindingResult errors, @PathVariable("id") long id ){
		if(errors.hasErrors()){
			return answer(answersForm,id,paginationForm);
		}
		String email = SecurityContextHolder.getContext().getAuthentication().getName();

		as.create(answersForm.getBody(), email, id);

		String redirect = String.format("redirect:/question/view/%d",id);
		ModelAndView mav = new ModelAndView(redirect);
        AuthenticationUtils.authorizeInView(mav, us);
		return mav;
	}

	@RequestMapping(path = "/question/answer/{id}/vote" , method = RequestMethod.POST)
	public ModelAndView votesAnswer(@PathVariable("id") long id, @RequestParam("vote") Boolean vote){
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		Optional<Answer> answer = as.answerVote(id,vote,email); // todo hay que hacer algo si no existe la rta (pag de error ?)

		String redirect = String.format("redirect:/question/view/%d",answer.get().getQuestion().getId());
		ModelAndView mav = new ModelAndView(redirect);
        AuthenticationUtils.authorizeInView(mav, us);
		return mav;
	}

	@RequestMapping(path = "/question/{id}/vote" , method = RequestMethod.POST)
	public ModelAndView votesQuestion( @PathVariable("id") long id, @RequestParam("vote") Boolean vote){
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		Optional<Question> question = qs.questionVote(id,vote,email); // todo hay que hacer algo si no existe la preg (pag de error ?)

		String redirect = String.format("redirect:/question/view/%d",id);
		ModelAndView mav = new ModelAndView(redirect);
        AuthenticationUtils.authorizeInView(mav, us);
		return mav;
	}


	@RequestMapping(path ="/question/answer/{id}/verify/", method = RequestMethod.POST)
	public ModelAndView verifyAnswer(@PathVariable("id") long id, @RequestParam("verify") boolean verify){

		Optional<Answer> answer = as.verify(id, verify);
		String redirect = String.format("redirect:/question/view/%d",answer.get().getQuestion().getId());
		ModelAndView mav = new ModelAndView(redirect);
        AuthenticationUtils.authorizeInView(mav, us);
		return mav;
	}

	@RequestMapping(path ="/question/answer/{id}/delete", method = RequestMethod.POST)
	public ModelAndView deleteAnswer(@PathVariable("id") long id){
		Long idQuestion = as.findById(id).get().getQuestion().getId();
		as.deleteAnswer(id);
		String redirect = String.format("redirect:/question/view/%d",idQuestion);
		ModelAndView mav = new ModelAndView(redirect);
		AuthenticationUtils.authorizeInView(mav, us);
		return mav;
	}





	@RequestMapping("/question/ask/community")
	public ModelAndView pickCommunity(){
		ModelAndView mav = new ModelAndView("question/ask/community");
        Optional<User> maybeUser = AuthenticationUtils.authorizeInView(mav, us);
		mav.addObject("communityList", cs.list(maybeUser.orElse(null)));
		return mav;
	}

	@RequestMapping(path = "/question/ask/content" , method = RequestMethod.GET)
	public ModelAndView createQuestionGet(@RequestParam("communityId") Number id , @ModelAttribute("questionForm") QuestionForm form){
		ModelAndView mav = new ModelAndView("question/ask/content");
        Optional<User> maybeUser = AuthenticationUtils.authorizeInView(mav, us);
		Community c = cs.findById(id.longValue()).orElseThrow(NoSuchElementException::new);

		mav.addObject("community", c);
		mav.addObject("forumList", fs.findByCommunity(id));
		return mav;
	}

	@RequestMapping(path = "/question/ask/content" , method = RequestMethod.POST)
	public ModelAndView createQuestionPost(@ModelAttribute("questionForm") @Valid QuestionForm form , BindingResult errors){
		if(errors.hasErrors()){
			return createQuestionGet(form.getCommunity() , form);
		}
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		StringBuilder path = new StringBuilder("redirect:/question/ask/finish");
		try {
			Optional<Question> question = qs.create(form.getTitle(), form.getBody(), email, form.getForum(), form.getImage().getBytes());
			question.ifPresent(q -> path.append("?id=").append(q.getId()));
		}catch (IOException e ) {
			LOGGER.error("Error leyendo los bytes de la imagen");
			Optional<Question> question = qs.create(form.getTitle(), form.getBody(), email, form.getForum(), null);
			question.ifPresent(q -> path.append("?id=").append(q.getId()));
		}
        ModelAndView mav = new ModelAndView(path.toString());
        AuthenticationUtils.authorizeInView(mav, us);
		return mav;
	}

	@RequestMapping("/question/ask/finish")
	public ModelAndView uploadQuestion(@RequestParam(value = "id", required = false) Number id){
		ModelAndView mav = new ModelAndView("question/ask/finish");
        Optional<User> maybeUser = AuthenticationUtils.authorizeInView(mav, us);

		if(id != null){ //La creación fue exitosa
			Optional<Question> q = qs.findById(maybeUser.orElse(null), id.longValue());
			q.ifPresent(question -> mav.addObject("question", question));
		}
		mav.addObject("success", id != null);

		return  mav;
	}

 */




