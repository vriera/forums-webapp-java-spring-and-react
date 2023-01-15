package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.interfaces.services.exceptions.CantAccess;
import ar.edu.itba.paw.models.Answer;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface AnswersService {

    Optional<Answer> findById(Long id);

    List<Answer> findByQuestion(Long idQuestion, int limit, int page, User current);

    List<Answer> getAnswers(int limit, int page, User current);

    Optional<Answer> create(String body, String email, Long idQuestion, String BaseUrl) throws CantAccess;

    void answerVote(Answer answer, Boolean vote, String email) throws CantAccess;

    Optional<Answer> verify(Long id, boolean bool);

    Optional<Long> countAnswers(long question);

    void deleteAnswer(Long id);
}
