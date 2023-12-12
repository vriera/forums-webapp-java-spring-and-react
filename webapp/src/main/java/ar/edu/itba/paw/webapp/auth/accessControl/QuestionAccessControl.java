package ar.edu.itba.paw.webapp.auth.accessControl;


import ar.edu.itba.paw.interfaces.services.QuestionService;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.controller.utils.Commons;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Component
public class QuestionAccessControl {

    @Autowired
    private Commons commons;

    @Autowired
    private AccessControl ac;

    @Autowired
    private CommunityAccessControl cas;

    @Autowired
    private QuestionService qs;


    @Transactional(readOnly = true)
    public boolean canAccess(long questionId){
        return canAccess(commons.currentUser(),questionId);
    }
    @Transactional(readOnly = true)
    public boolean canAccess(long userId, long questionId)  {
        return canAccess(ac.checkUser(userId) , questionId);
    }

    @Transactional(readOnly = true)
    public boolean canAccess(User user, long questionId) {
        try {
            Question question = qs.findById(questionId);
            return cas.canAccess(user , question.getForum().getCommunity().getId());
        }catch (NoSuchElementException e ){
            return true;
        }

    }



}
