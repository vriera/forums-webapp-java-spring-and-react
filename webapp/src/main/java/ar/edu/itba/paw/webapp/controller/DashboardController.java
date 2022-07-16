package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.interfaces.services.CommunityService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Answer;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.dto.DashboardAnswerListDto;
import ar.edu.itba.paw.webapp.dto.DashboardQuestionListDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Optional;

@Component
@Path("dashboard")
public class DashboardController {
    @Autowired
    private UserService us;

    @Autowired
    private CommunityService cs;


    @Context
    private UriInfo uriInfo;

    @GET
    @Path("questions")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response userQuestions(@DefaultValue("1") @QueryParam("page") int page){


        Optional<User> user = us.findByEmail("cruz.anitaa@hotmail.com");
        User u = user.orElse(null);
        //  User u = commons.currentUser();
        if(u ==null){
            return  Response.status(403).build();
        }
        List<Question> ql = us.getQuestions(u.getId() , page);
        DashboardQuestionListDto qlDto = DashboardQuestionListDto.questionListToQuestionListDto(ql , uriInfo , page , 5 ,us.getPageAmountForQuestions(u.getId()));

        return Response.ok(
                new GenericEntity<DashboardQuestionListDto>(qlDto){}
        ).build();

    }

    @GET
    @Path("answers")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response userAnswers(@DefaultValue("1") @QueryParam("page") int page){

        Optional<User> user = us.findByEmail("cruz.anitaa@hotmail.com");
        User u = user.orElse(null);
        //  User u = commons.currentUser();
        if(u ==null){
            return  Response.status(403).build();
        }
        List<Answer> al = us.getAnswers(u.getId() , page);
        DashboardAnswerListDto alDto = DashboardAnswerListDto.answerListToQuestionListDto(al , uriInfo , page , 5 ,us.getPageAmountForAnswers(u.getId()));

        return Response.ok(
                new GenericEntity<DashboardAnswerListDto>(alDto){}
        ).build();

    }

}
