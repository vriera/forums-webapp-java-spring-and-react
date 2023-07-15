package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Answer;
import ar.edu.itba.paw.models.AnswerVotes;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface AnswersService {


    Optional<AnswerVotes> getAnswerVote(Long id, Long userId);
    Optional<Answer> findById(Long id);

    List<Answer> findByQuestion(Long questionId, int page);

    Optional<Answer> create(String body, String email, Long questionId, String BaseUrl);

    Boolean answerVote(Answer answer, Boolean vote, String email);

    Optional<Answer> verify(Long id, boolean bool);

    int findByQuestionCount(Long questionId);

    void deleteAnswer(Long id);

}
