package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.webapp.controller.utils.AuthenticationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ErrorController {

	@Autowired
	UserService us;

	@RequestMapping("/403")
	public ModelAndView forbidden() {
		ModelAndView mav = new ModelAndView("/error/403");
		AuthenticationUtils.authorizeInView(mav, us);
		return mav;
	}

	@RequestMapping("/500")
	public ModelAndView error(){
		ModelAndView mav = new ModelAndView("/error/500");
		AuthenticationUtils.authorizeInView(mav, us);
		return  mav;
	}

	@RequestMapping("/404")
	public ModelAndView notFound(){
		ModelAndView mav = new ModelAndView("/error/404");
		AuthenticationUtils.authorizeInView(mav, us);
		return  mav;
	}

}
