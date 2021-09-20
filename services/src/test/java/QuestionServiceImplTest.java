import ar.edu.itba.paw.interfaces.persistance.QuestionDao;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.services.QuestionServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.junit.Assert;
import java.sql.Timestamp;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class QuestionServiceImplTest {
	private static final String TITLE = "SAMPLE TITLE";
	private static final String BODY = "SAMPLE BODY";
	private static final String EMAIL = "example@email.com";
	private static final String USERNAME = "user";
	private static final String COMMUNITY_NAME = "sample community name";
	private static final String COMMUNITY_DESCRIPTION = "Sample description";
	private static final String FORUM_NAME= "sample forum name";
	private static final User OWNER = new User(1,USERNAME, EMAIL);
	private static final User MODERATOR = new User(1,USERNAME, EMAIL);
	private static final  Community COMMUNITY = new Community(1L, COMMUNITY_NAME, COMMUNITY_DESCRIPTION, MODERATOR);
	private static final Forum FORUM = new Forum( 1L, FORUM_NAME, COMMUNITY);

	@InjectMocks
	private QuestionServiceImpl questionService = new QuestionServiceImpl();
	@Mock
	private QuestionDao mockDao;
	@Mock
	private UserService mockUserService;

	@Test
	public void testCreateUserExists(){
		Mockito.when(mockUserService.findByEmail(EMAIL)).thenReturn(Optional.of(OWNER));
		Mockito.when(mockDao.create(TITLE, BODY, OWNER, FORUM))
				.thenReturn(new Question(1L,new SmartDate(new Timestamp(System.currentTimeMillis())), TITLE,BODY,OWNER,COMMUNITY , FORUM ));

		Optional<Question> q = questionService.create(TITLE, BODY, OWNER, FORUM);

		Assert.assertNotNull(q);
		Assert.assertTrue(q.isPresent());
		Assert.assertEquals(TITLE, q.get().getTitle());
		Assert.assertEquals(BODY, q.get().getBody());
		Assert.assertEquals(OWNER, q.get().getOwner());
		Assert.assertEquals(COMMUNITY, q.get().getCommunity());
	}

	@Test
	public void testCreateUserDoesntExist(){
		Mockito.when(mockUserService.findByEmail(EMAIL)).thenReturn(Optional.empty());
		Mockito.when(mockDao.create(Mockito.eq(TITLE), Mockito.eq(BODY), Mockito.eq(OWNER), Mockito.eq(FORUM)))
				.thenReturn(new Question(1L,new SmartDate(new Timestamp(System.currentTimeMillis())), TITLE,BODY,OWNER,COMMUNITY,FORUM));
		Mockito.when(mockUserService.create(Mockito.eq(USERNAME), Mockito.eq(EMAIL))).thenReturn(Optional.of(OWNER));

		Optional<Question> q = questionService.create(TITLE, BODY, OWNER , FORUM);

		Assert.assertNotNull(q);
		Assert.assertTrue(q.isPresent());
		Assert.assertEquals(TITLE, q.get().getTitle());
		Assert.assertEquals(BODY, q.get().getBody());
		Assert.assertEquals(OWNER, q.get().getOwner());
		Assert.assertEquals(COMMUNITY, q.get().getCommunity());
	}

	@Test //TODO: completar test
	public void testCreateNoTitle(){

	}

}
