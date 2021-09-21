package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.Answer;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.webapp.form.AnswersForm;
import ar.edu.itba.paw.webapp.form.QuestionForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class QuestionController {
	@Autowired
	private AnswersService as;

	@Autowired
	private CommunityService cs;

	@Autowired
	private ForumService fs;

	@Autowired
	private QuestionService qs;

	@RequestMapping("/question/view/{id}")
	public ModelAndView answer(@ModelAttribute("answersForm") AnswersForm answersForm, @PathVariable("id") long id){
		ModelAndView mav = new ModelAndView("/question/view");
		List<Answer> answersList = as.findByQuestionId(id);
		Optional<Question> question = qs.findById(id);
		mav.addObject("answerList", answersList);
		mav.addObject("question",question.get()); //falta verificar que exista la pregunta

		//FIXME: Cambiar esto a que no se clave la comunidad actual
		mav.addObject("communityList", cs.list().stream().filter(community -> community.getId() != question.get().getCommunity().getId().longValue()).collect(Collectors.toList()));

		return mav;
	}

	@RequestMapping(path = "/question/{id}/answer" , method = RequestMethod.POST)
	public ModelAndView createAnswerPost(@ModelAttribute("answersForm") AnswersForm answersForm, @PathVariable("id") long id ){
		String email = SecurityContextHolder.getContext().getAuthentication().getName();

		as.create(answersForm.getBody(), email, id);

		String redirect = String.format("redirect:/question/view/%d",id);
		return new ModelAndView(redirect);
	}

	@RequestMapping("/question/answer/{id}/verify/")
	public ModelAndView verifyAnswer(@PathVariable("id") long id){

		Optional<Answer> answer = as.verify(id);
		String redirect = String.format("redirect:/question/view/%d",answer.get().getId_question());
		return new ModelAndView(redirect);
	}

	@RequestMapping("/question/ask/community")
	public ModelAndView pickCommunity(){
		ModelAndView mav = new ModelAndView("question/ask/community");

		mav.addObject("communityList", cs.list());

		return mav;
	}

	@RequestMapping(path = "/question/ask/content" , method = RequestMethod.GET)
	public ModelAndView createQuestionGet(@RequestParam("communityId") Number id , @ModelAttribute("questionForm") QuestionForm form){
		ModelAndView mav = new ModelAndView("question/ask/content");

		Community c = cs.findById(id.longValue()).orElseThrow(NoSuchElementException::new);

		mav.addObject("community", c);
		mav.addObject("forumList", fs.findByCommunity(id));
		return mav;
	}

	@RequestMapping(path = "/question/ask/content" , method = RequestMethod.POST)
	public ModelAndView createQuestionPost( @ModelAttribute("questionForm") QuestionForm form){
		String email = SecurityContextHolder.getContext().getAuthentication().getName();

		Optional<Question> question = qs.create(form.getTitle(), form.getBody(), email, form.getForum());
		StringBuilder path = new StringBuilder("redirect:/question/ask/finish");
		question.ifPresent(q -> path.append("?id=").append(q.getId()));

		return new ModelAndView(path.toString());
	}

	@RequestMapping("/question/ask/finish")
	public ModelAndView uploadQuestion(@RequestParam(value = "id", required = false) Number id){
		ModelAndView mav = new ModelAndView("question/ask/finish");
		if(id != null){ //La creación fue exitosa
			Optional<Question> q = qs.findById(id.longValue());
			q.ifPresent(question -> mav.addObject("question", question));
		}
		mav.addObject("success", id != null);

		return  mav;
	}


}
