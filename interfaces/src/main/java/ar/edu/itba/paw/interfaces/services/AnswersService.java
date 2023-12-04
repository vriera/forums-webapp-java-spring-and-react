package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Answer;
import ar.edu.itba.paw.models.AnswerVotes;
import ar.edu.itba.paw.models.User;

import java.util.List;
import java.util.Optional;

public interface AnswersService {


    AnswerVotes getAnswerVote(Long id, Long userId);
    Answer findById(Long id);

    List<Answer> findByQuestion(Long questionId, int page);

    Answer create(String body, String email, Long questionId, String BaseUrl);

    Boolean answerVote(Answer answer, Boolean vote, String email);

    Answer verify(Long id, boolean bool);

    int findByQuestionCount(Long questionId);

    void deleteAnswer(Long id);

    List<AnswerVotes> findVotesByAnswerId(Long answerId , Long userId , int page);

    int findVotesByAnswerIdCount(Long answerId, Long userId);
}
