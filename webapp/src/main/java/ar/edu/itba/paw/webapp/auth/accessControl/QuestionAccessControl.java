package ar.edu.itba.paw.webapp.auth.accessControl;


import ar.edu.itba.paw.interfaces.services.CommunityService;
import ar.edu.itba.paw.interfaces.services.QuestionService;
import ar.edu.itba.paw.models.Answer;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.controller.Commons;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
    public boolean canAccess(long questionId)  throws NotFoundException{
        return canAccess(commons.currentUser(),questionId);
    }
    @Transactional(readOnly = true)
    public boolean canAccess(long userId, long questionId)  throws NotFoundException{
        return canAccess(ac.checkUser(userId) , questionId);
    }

    @Transactional(readOnly = true)
    public boolean canAccess(User user, long questionId) throws NotFoundException {

        Optional<Question> maybeQuestion = qs.findById(questionId);

        if(!maybeQuestion.isPresent() )
            throw new NotFoundException("");

        return cas.canAccess(user , maybeQuestion.get().getForum().getCommunity().getId());
    }



}
