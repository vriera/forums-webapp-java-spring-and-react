package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.interfaces.services.CommunityService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Answer;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.controller.utils.GenericResponses;
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

    @Autowired
    private Commons commons;

    @Context
    private UriInfo uriInfo;

    @GET
    @Path("questions")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response userQuestions(@DefaultValue("1") @QueryParam("page") int page){

        User u = commons.currentUser();
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

         User u = commons.currentUser();
        if(u ==null){
            return  Response.status(403).build();
        }
        List<Answer> al = us.getAnswers(u.getId() , page);
        DashboardAnswerListDto alDto = DashboardAnswerListDto.answerListToQuestionListDto(al , uriInfo , page , 5 ,us.getPageAmountForAnswers(u.getId()));

        return Response.ok(
                new GenericEntity<DashboardAnswerListDto>(alDto){}
        ).build();

    }



























//Esto va en dashboard??? o se arma un enpoint de moderation
//O se arma un endpoint de otra cosa
//TODO donde se pone esto y como armamos los enpoints

    @PUT
    @Path("/{userId}/unblock/{communityId}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response unblock(@PathParam("communityId") final int communityId , @PathParam("userId") final int userId){
        final User u = commons.currentUser();
        if( u.getId() != userId){
            return GenericResponses.notAuthorized();
        }

        boolean success = cs.unblockCommunity(u.getId(), communityId);

        if(!success){
            return GenericResponses.badRequest();
        }

        return GenericResponses.success();

    }



    @PUT
    @Path("/{userId}/block/{communityId}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response block(@PathParam("communityId") final int communityId , @PathParam("userId") final int userId){
        final User u = commons.currentUser();
        if( u.getId() != userId){
            return GenericResponses.notAuthorized();
        }

        boolean success = cs.blockCommunity(u.getId(), communityId);

        if(!success){
            return GenericResponses.badRequest();
        }

        return GenericResponses.success();

    }



    //IDEA: manejar todo el acess en un solo enpoint y utilizar el json para definir la accion
    @PUT
    @Path("/{userId}/request-access/{communityId}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response requestAccess(@PathParam("communityId") final int communityId , @PathParam("userId") final int userId){
        final User u = commons.currentUser();
        if( u.getId() != userId){
            return GenericResponses.notAuthorized();
        }

        boolean success = cs.requestAccess(u.getId(), communityId);

        if(!success){
            return GenericResponses.badRequest();
        }

        return GenericResponses.success();

    }


    @PUT
    @Path("/{moderatorId}/access/{communityId}/admit/{userId}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response admitAccess(@PathParam("communityId") final int communityId , @PathParam("moderatorId") final int moderatorId , @PathParam("userId") final int userId){
        final User u = commons.currentUser();
        if( u.getId() != moderatorId){
            return GenericResponses.notAuthorized();
        }

        boolean success = cs.admitAccess(userId, communityId , u);

        if(!success){
            return GenericResponses.badRequest();
        }

        return GenericResponses.success();

    }

    @PUT
    @Path("/{moderatorId}/access/{communityId}/reject/{userId}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response rejectAccess(@PathParam("communityId") final int communityId , @PathParam("moderatorId") final int moderatorId , @PathParam("userId") final int userId){
        final User u = commons.currentUser();

        if( u.getId() != moderatorId){
            return GenericResponses.notAuthorized();
        }

        boolean success = cs.rejectAccess(userId, communityId , u);

        if(!success){
            return GenericResponses.badRequest();
        }

        return GenericResponses.success();

    }

}
