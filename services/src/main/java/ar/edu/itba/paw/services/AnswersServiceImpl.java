package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistance.AnswersDao;
import ar.edu.itba.paw.interfaces.services.AnswersService;
import ar.edu.itba.paw.interfaces.services.MailingService;
import ar.edu.itba.paw.interfaces.services.QuestionService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Answer;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@Service
public class AnswersServiceImpl implements AnswersService {

    @Autowired
    private AnswersDao answerDao;

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private MailingService mailingService;

    @Override
    public List<Answer> findByQuestion(long idQuestion, int limit, int offset){
        return answerDao.findByQuestion(idQuestion, limit, offset);
    }

    public Optional<Answer> verify(Long id, boolean bool){
        return answerDao.verify(id, bool);
    }

    @Override
    public Optional<Long> countAnswers(long question) {
        return answerDao.countAnswers(question);
    }

    @Override
    public Optional<Answer> findById(long id) {
        return answerDao.findById(id);
    }

       @Override
       @Transactional
    public Optional<Answer> create(String body, String email, Long idQuestion) {
        if(body == null || idQuestion == null || email == null )
            return Optional.empty();

        Optional<User> u = userService.findByEmail(email);
        Optional<Question> q = questionService.findById(idQuestion);

        if(!q.isPresent() || !u.isPresent())
            return Optional.empty();

        Optional<Answer> a = Optional.ofNullable(answerDao.create(body ,u.get(), idQuestion));
        a.ifPresent(answer ->
                mailingService.sendAnswerVerify(q.get().getOwner().getEmail(), q.get(), answer,   ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString())
        );

        return a;
    }

    @Override
    @Transactional
    public Optional<Answer> answerVote(Long idAnswer, Boolean vote, String email) {
        if(idAnswer == null || vote == null || email == null)
            return Optional.empty();
        Optional<Answer> a = findById(idAnswer);
        Optional<User> u = userService.findByEmail(email);
        if(!a.isPresent() || !u.isPresent())
            return Optional.empty();

        answerDao.addVote(vote,u.get().getId(),idAnswer);
        return a;
    }
}
