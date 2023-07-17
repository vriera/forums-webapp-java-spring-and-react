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
    public boolean canAccess(long answerId) throws NotFoundException{
        return canAccess(commons.currentUser() , answerId);
    }

    @Transactional(readOnly = true)
    public boolean canAccess(long userId, long answerId ) throws NotFoundException{
       return canAccess(ac.checkUser(userId) ,answerId);
    }
    @Transactional(readOnly = true)
    public boolean canAccess(User u , long answerId) throws NotFoundException{

        Optional<Answer> answer = as.findById(answerId);

        if(!answer.isPresent())
            throw new NotFoundException("");
        return cas.canAccess(u , answer.get().getQuestion().getForum().getCommunity().getId());
    }



    @Transactional(readOnly = true)
    public boolean canVerify( long answerId) throws NotFoundException {
        User u = commons.currentUser();
        Optional<Answer> a = as.findById(answerId);

        if(!a.isPresent())
            throw new NotFoundException("");

        Optional<Question> q = qs.findById( a.get().getQuestion().getId());
        if(!q.isPresent())
            throw new NotFoundException("");

        return u != null && u.getId() == q.get().getOwner().getId();
    }

}
