package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.exceptions.AlreadyCreatedException;
import ar.edu.itba.paw.interfaces.exceptions.GenericOperationException;
import ar.edu.itba.paw.interfaces.persistance.UserDao;
import ar.edu.itba.paw.models.User;
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
	public void testCreateEmptyEmail() throws GenericOperationException {
		Optional<User> maybeUser = userService.create(USERNAME, "", PASSWORD,"");

    	Assert.assertNotNull(maybeUser);
		Assert.assertFalse(maybeUser.isPresent());


	}

	@Test
	public void testCreateEmptyUsername() throws GenericOperationException {

		Optional<User> maybeUser = userService.create("", EMAIL, PASSWORD,"");

		Assert.assertNotNull(maybeUser);
		Assert.assertFalse(maybeUser.isPresent());

	}

	@Test
	public void testCreateEmptyPassword() throws GenericOperationException {
		Optional<User> maybeUser = userService.create(USERNAME, EMAIL, "","");

		Assert.assertNotNull(maybeUser);
		Assert.assertFalse(maybeUser.isPresent());
	}

	@Test
	public void testCreateAlreadyExists() throws GenericOperationException {
		Mockito.when(mockDao.findByEmail(USERNAME)).thenReturn(Optional.of(new User(1L,USERNAME, EMAIL, PASSWORD)));

		Optional<User> maybeUser = userService.create(USERNAME, EMAIL, PASSWORD,"");

		Assert.assertNotNull(maybeUser);
		Assert.assertFalse(maybeUser.isPresent());


	}

	@Test
	public void testCreateAlreadyExistsNoPassword() throws GenericOperationException {

		Mockito.when(mockDao.findByEmail(USERNAME)).thenReturn(Optional.of(new User(1L,USERNAME, EMAIL, "")));

		Optional<User> maybeUser = userService.create(USERNAME, EMAIL, PASSWORD,"");

		Assert.assertNotNull(maybeUser);
		Assert.assertFalse(maybeUser.isPresent());


	}
}