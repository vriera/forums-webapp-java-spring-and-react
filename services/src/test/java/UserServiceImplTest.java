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
	private static final String EMAIL = "email@example.com";
	private static final String USERNAME = "username";
	private static final String PASSWORD = "contraseña";

	@InjectMocks
	private UserServiceImpl userService = new UserServiceImpl();

	@Mock
	private UserDao mockDao;

	@Test
	public void testCreateEmptyEmail() {
		Optional<User> maybeUser = userService.create(USERNAME, "", PASSWORD);

    	Assert.assertNotNull(maybeUser);
		Assert.assertFalse(maybeUser.isPresent());
	}

	@Test
	public void testCreateEmptyUsername(){
		Optional<User> maybeUser = userService.create("", EMAIL, PASSWORD);

		Assert.assertNotNull(maybeUser);
		Assert.assertFalse(maybeUser.isPresent());
	}

	@Test
	public void testCreateEmptyPassword(){
		Optional<User> maybeUser = userService.create(USERNAME, EMAIL, "");

		Assert.assertNotNull(maybeUser);
		Assert.assertFalse(maybeUser.isPresent());
	}

	@Test
	public void testCreateAlreadyExists() {
		Mockito.when(mockDao.findByEmail(USERNAME)).thenReturn(Optional.of(new User(1L,USERNAME, EMAIL, PASSWORD)));

		Optional<User> maybeUser = userService.create(USERNAME, EMAIL, PASSWORD);

		Assert.assertNotNull(maybeUser);
		Assert.assertFalse(maybeUser.isPresent());
	}

	@Test
	public void testCreateAlreadyExistsNoPassword(){
		Mockito.when(mockDao.findByEmail(USERNAME)).thenReturn(Optional.of(new User(1L,USERNAME, EMAIL, "")));

		Optional<User> maybeUser = userService.create(USERNAME, EMAIL, PASSWORD);

		Assert.assertNotNull(maybeUser);
		Assert.assertFalse(maybeUser.isPresent());
	}
}