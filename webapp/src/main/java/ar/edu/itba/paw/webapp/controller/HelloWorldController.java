package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;

@Controller
public class HelloWorldController {
    @Autowired
    UserService us;

    @RequestMapping("/")
    public ModelAndView index(@RequestParam(value = "userId", required = true) final int id) {
        final ModelAndView mav = new ModelAndView("index");
        final Optional<User> u = us.findById(id);
        mav.addObject("user", u.get());
        return mav;
    }

    @RequestMapping("/create")
    public ModelAndView create(@RequestParam(value = "name", required = true) final String username, @RequestParam(value="password") final String password) {
        final Optional<User> u = us.create(username, password);
        return new ModelAndView("redirect:/?userId=" + (u.isPresent()? u.get().getUserid() : -1));
    }

    @RequestMapping("/community")
    public ModelAndView community(){
        ModelAndView mav = new ModelAndView("community");

        mav.addObject("community_name", "Matemática");

        String[] questions = {"Hola! no se como derivar, estoy bastante perdido. Alguien me podria dar una breve explicacion?",
        "Estoy tratando de resolver la integral de x al cuadrado pero la gorda de matematica no me lo explico bien. Cuanto daria?",
        "Necesito corroborar que la suma de 5-4 da 1, por que mi calculadora se quedó sin batería.",
        "Estoy tratando de dibujar una parábola de forma x al cuadrado, eso sería una carita feliz, no?"};
        mav.addObject("question_list", questions);


        return mav;
    }
}