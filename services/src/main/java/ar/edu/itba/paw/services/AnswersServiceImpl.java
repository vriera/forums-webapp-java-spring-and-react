package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistance.AnswersDao;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;

import ar.edu.itba.paw.services.utils.PaginationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class AnswersServiceImpl implements AnswersService {
    private static final int PAGE_SIZE = PaginationUtils.PAGE_SIZE;
    @Autowired
    private AnswersDao answerDao;

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private MailingService mailingService;

    private static final Logger LOGGER = LoggerFactory.getLogger(AnswersServiceImpl.class);

    @Override
    public AnswerVotes getAnswerVote(long id, long userId) {
        return answerDao.findVote(id, userId).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public List<Answer> findByQuestion(long questionId, int page) {
        boolean idIsInvalid = questionId < 0;
        boolean pageIsInvalid = page < 0;
        if (idIsInvalid || pageIsInvalid)
            return Collections.emptyList();

        return answerDao.findByQuestion(questionId, PAGE_SIZE, page * PAGE_SIZE);
    }

    @Override
    public long findByQuestionPagesCount(Long questionId) {
        return PaginationUtils.getPagesFromTotal(answerDao.findByQuestionCount(questionId));
    }

    public Answer verify(long answerId, boolean bool) {
        return answerDao.verify(answerId, bool).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public Answer findById(Long id) {
        return answerDao.findById(id).orElseThrow(NoSuchElementException::new);
    }

    @Override
    @Transactional
    public Answer create(String body, User user, long questionId, String baseUrl) {
        if (body == null || user == null)
            throw new IllegalArgumentException();

        Question q = questionService.findById(questionId);

        Answer a = answerDao.create(body, user, q);

        mailingService.sendAnswerVerify(q.getOwner().getEmail(), q, a, baseUrl, LocaleContextHolder.getLocale());

        return a;
    }

    @Override
    @Transactional
    public Boolean answerVote(long answerId, Boolean vote, long userId) {
        User u = userService.findById(userId);

        answerDao.addVote(vote, u, answerId);
        return true;
    }

    // Vote lists
    @Override
    public List<AnswerVotes> findVotesByAnswerId(long answerId, Long userId, int page) {
        if (!(userId == null || userId < 0)) {
            List<AnswerVotes> answerVotesList = new ArrayList<>();
            if (page == 0) {
                try {
                    answerVotesList.add(getAnswerVote(answerId, userId));
                } catch (NoSuchElementException ignored) {
                }
            }
            return answerVotesList;
        }
        return answerDao.findVotesByAnswerId(answerId, PAGE_SIZE, page * PAGE_SIZE);
    }

    @Override
    public long findVotesByAnswerIdPagesCount(long answerId, Long userId) {
        if (!(userId == null || userId < 0)) {
            try {
                getAnswerVote(answerId, userId);
                return 1;
            } catch (NoSuchElementException ignored) {
            }
            return 0;
        }
        return PaginationUtils.getPagesFromTotal(answerDao.findVotesByAnswerIdCount(answerId));
    }

}
