package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Answer;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.NoSuchElementException;

@Controller
public class DashboardController {

	@Autowired
	UserService us;

	@RequestMapping("/dashboard/view/questions")
	public ModelAndView viewQuestions(@RequestParam("page") Number page){
		ModelAndView mav = new ModelAndView("dashboard/view/questions");

		User currentUser = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(NoSuchElementException::new);
		List<Question> questions = us.getQuestions(currentUser.getId(), page);

		mav.addObject("currentUser", currentUser);
		mav.addObject("questions", questions);

		return mav;
	}

	@RequestMapping("/dashboard/view/answers")
	public ModelAndView viewAnswers(@RequestParam("page") Number page){
		ModelAndView mav = new ModelAndView("dashboard/view/answers");

		User currentUser = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(NoSuchElementException::new);
		List<Answer> answers = us.getAnswers(currentUser.getId(), page);

		mav.addObject("currentUser", currentUser);
		mav.addObject("answers", answers);

		return mav;
	}

	@RequestMapping("/dashboard/view/communities")
	public ModelAndView viewCommunities(@RequestParam("page") Number page){
		ModelAndView mav = new ModelAndView("dashboard/view/communities");

		User currentUser = us.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(NoSuchElementException::new);
		List<Community> communities = us.getModeratedCommunities(currentUser.getId(), page);

		mav.addObject("currentUser", currentUser);
		mav.addObject("communities", communities);

		return mav;
	}
}
