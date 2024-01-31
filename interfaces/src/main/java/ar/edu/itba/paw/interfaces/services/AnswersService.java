package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.interfaces.exceptions.BadParamsException;
import ar.edu.itba.paw.interfaces.exceptions.GenericNotFoundException;
import ar.edu.itba.paw.models.Answer;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface AnswersService {

    Optional<Answer> findById(Long id);

    List<Answer> findByQuestion(Long idQuestion, int limit, int page, User current);

    List<Answer> getAnswers(int limit, int page, Long userId, Long questionId) throws BadParamsException, GenericNotFoundException;

    Optional<Answer> create(String body, User user, Long idQuestion, String baseUrl) throws GenericNotFoundException, BadParamsException;

    void answerVote(long answer, User user, Boolean vote) throws BadParamsException, GenericNotFoundException;

    Optional<Answer> verify(Long id, boolean bool) throws GenericNotFoundException;

    Optional<Long> countAnswers(Long question,Long userId);

    void deleteAnswer(Long id);
}
