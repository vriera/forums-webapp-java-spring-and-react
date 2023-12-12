package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Answer;
import ar.edu.itba.paw.models.AnswerVotes;
import ar.edu.itba.paw.models.User;

import java.util.List;

public interface AnswersService {
    AnswerVotes getAnswerVote(long id, long userId);

    Answer findById(Long id);

    List<Answer> findByQuestion(long questionId, int page);

    Answer create(String body, User user, long questionId, String BaseUrl);

    Boolean answerVote(long answerId, Boolean vote, long userId);

    Answer verify(long answerId, boolean bool);

    long findByQuestionPagesCount(Long questionId);

    List<AnswerVotes> findVotesByAnswerId(long answerId , Long userId , int page);

    long findVotesByAnswerIdPagesCount(long answerId, Long userId);
}
