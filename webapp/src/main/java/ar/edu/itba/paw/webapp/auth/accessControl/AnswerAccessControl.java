package ar.edu.itba.paw.webapp.auth.accessControl;

import ar.edu.itba.paw.interfaces.services.AnswersService;
import ar.edu.itba.paw.interfaces.services.QuestionService;
import ar.edu.itba.paw.models.Answer;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.controller.Commons;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

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
    public boolean canAccess(long answerId) throws NoSuchElementException{
        return canAccess(commons.currentUser() , answerId);
    }

    @Transactional(readOnly = true)
    public boolean canAccess(long userId, long answerId ) throws NoSuchElementException{
       return canAccess(ac.checkUser(userId) ,answerId);
    }
    @Transactional(readOnly = true)
    public boolean canAccess(User u , long answerId) throws NoSuchElementException{

        Answer answer = as.findById(answerId);
        return cas.canAccess(u , answer.getQuestion().getForum().getCommunity().getId());
    }



    @Transactional(readOnly = true)
    public boolean canVerify( long answerId) {
        User u = commons.currentUser();
        Answer a = as.findById(answerId);
        Question q = qs.findById( a.getId());


        if (u == null || u.getId() != q.getOwner().getId()) {
            return false;
        }
        return true;
    }

}
