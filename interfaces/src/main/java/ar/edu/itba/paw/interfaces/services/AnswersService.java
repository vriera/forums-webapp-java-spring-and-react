package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Answer;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface AnswersService {

    Optional<Answer> findById(Long id);

    public List<Answer> findByQuestion(Long idQuestion, int limit, int offset, User current);

    Optional<Answer> create(String body, String email, Long idQuestion);

    Optional<Answer> answerVote(Long idAnswer, Boolean vote, String email);

    Optional<Answer> verify(Long id, boolean bool);

    Optional<Long> countAnswers(long question);

    void deleteAnswer(Long id);
}
