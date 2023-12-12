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
    private Commons commons;


    @Transactional(readOnly = true)
    public boolean canAsk(HttpServletRequest request) {
        try {
            JSONObject object = AccessControlUtils.extractBodyAsJson(request);
            long id = object.getLong("questionId");
            return canAccess(id);
        }catch (Exception e){
            return true;
        }
    }
    @Transactional(readOnly = true)
    public boolean canAccess(long answerId) {
        return canAccess(commons.currentUser() , answerId);
    }

    @Transactional(readOnly = true)
    public boolean canAccess(long userId, long answerId ){
       return canAccess(ac.checkUser(userId) ,answerId);
    }

    @Transactional(readOnly = true)
    public boolean canAccess(User u , long answerId) {
        try {
            Answer answer = as.findById(answerId);
            return cas.canAccess(u, answer.getQuestion().getForum().getCommunity().getId());
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
