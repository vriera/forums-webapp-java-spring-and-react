package ar.edu.itba.paw.webapp.auth.accessControl;

import ar.edu.itba.paw.interfaces.services.AnswersService;
import ar.edu.itba.paw.interfaces.services.QuestionService;
import ar.edu.itba.paw.models.Answer;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.auth.accessControl.utils.AccessControlUtils;
import ar.edu.itba.paw.webapp.controller.utils.Commons;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.NoSuchElementException;

@Component
public class AnswerAccessControl {


    @Autowired
    private AnswersService as;

    @Autowired
    private QuestionService qs;

    @Autowired
    private AccessControl ac;

    @Autowired
    private CommunityAccessControl cas;


    @Autowired
    private QuestionAccessControl qas;

    @Autowired
    private Commons commons;


    @Transactional(readOnly = true)
    public boolean canAnswer(HttpServletRequest request) {
        try {
            JSONObject object = AccessControlUtils.extractBodyAsJson(request);
            long id = object.getLong("questionId");
            return qas.canAccess(id);
        }catch (Exception e){
            return true;
        }
    }


    @Transactional(readOnly = true)
    public boolean canSearch(HttpServletRequest request) {
        String questionIdString = request.getParameter("questionId");
        String ownerIdString = request.getParameter("ownerId");
        //Pasa a ser bad request en el controller
        if(questionIdString != null && ownerIdString != null)
            return true;

        try{
            if(questionIdString != null){
                long questionId = Long.parseLong(questionIdString);
                return qas.canAccess(questionId);
            }

            if(ownerIdString != null){
                long ownerId = Long.parseLong(ownerIdString);
                return ac.isLoggedUser(ownerId);
            }
            return true;

        }catch (NumberFormatException ignored){
            return  true;
        }

    }

    @Transactional(readOnly = true)
    public boolean canAccess(long answerId) {
        return canAccess( answerId , commons.currentUser());
    }

    @Transactional(readOnly = true)
    public boolean canAccess( long answerId  , long userId){
        return canAccess(answerId,ac.getUserIfIsRequester(userId));
    }

    private boolean canAccess( long answerId , User u ) {
        try {
            Answer answer = as.findById(answerId);
            return qas.canAccess(u, answer.getQuestion().getId());
        }catch (NoSuchElementException e ){
            return true;
        }
    }

    @Transactional(readOnly = true)
    public boolean canVerify( long answerId) {
        User u = commons.currentUser();
        try {
            Answer a = as.findById(answerId);
            Question q = qs.findById(a.getQuestion().getId());
            return u != null && u.getId() == q.getOwner().getId();
        }catch (NoSuchElementException e ){
            return true;
        }
    }

}
