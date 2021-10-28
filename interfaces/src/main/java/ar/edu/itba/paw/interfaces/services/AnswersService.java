package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.*;

import java.util.List;
import java.util.Optional;

public interface AnswersService {

    Optional<Answer> findById(Long id);

    List<Answer> findByQuestion(Long question, int limit, int offset);

    Optional<Answer> create(String body, String email, Long idQuestion);

    Optional<Answer> answerVote(Long idAnswer, Boolean vote, String email);

    Optional<Answer> verify(Long id, boolean bool);

    Optional<Long> countAnswers(long question);

    void deleteAnswer(Long id);
}
