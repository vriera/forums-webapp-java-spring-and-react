package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.exceptions.GenericOperationException;
import ar.edu.itba.paw.interfaces.persistance.QuestionDao;
import ar.edu.itba.paw.interfaces.services.ForumService;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Forum;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class QuestionServiceImplTest {
    @InjectMocks
    private QuestionServiceImpl questionService = new QuestionServiceImpl();

    @Mock
    private QuestionDao mockDao;

    @Mock
    private ForumService forumService;

    private User mockUser = new User(1L, "username", "email", "password");

    @Test(expected = GenericOperationException.class)
    public void testCreateInBadCommunity() throws GenericOperationException {
        questionService.create("Title 1", "body 1", mockUser, 1L, null);
        Assert.fail();
    }

    @Test()
    public void testCreate() throws GenericOperationException {
        Community community = new Community(1L, "name", "description", mockUser);
        Forum forum = new Forum(1L, "name", new Community(1L, "name", "description", mockUser));
        Question question = new Question(1L, Date.from(Instant.now()), "title", "body", mockUser, community, forum, null);
        Mockito.when(forumService.findByCommunity(forum.getCommunity().getId())).thenReturn(Arrays.asList(forum));
        Mockito.when(mockDao.create(question.getTitle(), question.getBody(), question.getOwner(), question.getForum(), null))
                .thenReturn(question);
        Optional<Question> optional = questionService.create(question.getTitle(), question.getBody(), question.getOwner(), question.getForum().getCommunity().getId(), null);
        Assert.assertTrue(optional.isPresent());
    }
}