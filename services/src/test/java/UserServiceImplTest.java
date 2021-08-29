import ar.edu.itba.paw.interfaces.persistance.UserDao;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.services.UserServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {
	private static final String PASSWORD = "passwordpassword";
	private static final String USERNAME = "username";
	@InjectMocks
	private UserServiceImpl userService = new UserServiceImpl();
	@Mock
	private UserDao mockDao;

	@Test
	public void testCreate() {
// 1. Setup!
		Mockito.when(mockDao.create(Mockito.eq(USERNAME),
						Mockito.eq(PASSWORD))).thenReturn(new User(USERNAME, PASSWORD, 1));
// 2. "ejercito" la class under test
		Optional<User> maybeUser
				= userService.create(USERNAME, PASSWORD);
// 3. Asserts!
		Assert.assertNotNull(maybeUser);
		Assert.assertTrue(maybeUser.isPresent());
		Assert.assertEquals(USERNAME, maybeUser.get().getUsername());
	}
	@Test
	public void testCreateEmptyPassword() {
// 1. Setup!
// 2. "ejercito" la class under test
		Optional<User> maybeUser
				= userService.create(USERNAME, "");
// 3. Asserts!
		Assert.assertNotNull(maybeUser);
		Assert.assertFalse(maybeUser.isPresent());
	}
	@Test
	public void testCreateAlreadyExists() {
// 1. Setup!
		//Mockito.when(mockDao.findByUsername(Mockito.eq(USERNAME))).thenReturn(Optional.of(new User(USERNAME, PASSWORD, 1)));
// 2. "ejercito" la class under test
		Optional<User> maybeUser = userService.create(USERNAME, PASSWORD);
// 3. Asserts!
		Assert.assertNotNull(maybeUser);
		Assert.assertFalse(maybeUser.isPresent());
	}
}