package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistance.UserDao;
import ar.edu.itba.paw.interfaces.services.MailingService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.exceptions.EmailTakenException;
import ar.edu.itba.paw.models.exceptions.IncorrectPasswordException;
import ar.edu.itba.paw.models.exceptions.UsernameTakenException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {
    private static final String EMAIL = "email@example.com";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String BASE_URL = "http://localhost:8080";
    private static final User USER = new User(1L, USERNAME, EMAIL, PASSWORD);
    @InjectMocks
    private UserServiceImpl userService = new UserServiceImpl();

    @Mock
    private UserDao mockDao;
    @Mock
    private PasswordEncoder encoder;
    @Mock
    private MailingService mailService;

    @Test
    public void testCreateWithEmptyFields() throws EmailTakenException, UsernameTakenException {
        Optional<User> result = userService.create("", "", "", BASE_URL);
        assertFalse(result.isPresent());
    }

    @Test(expected = EmailTakenException.class)
    public void testCreateWithExistingUser() throws EmailTakenException, UsernameTakenException {
        when(mockDao.findByEmail(EMAIL)).thenReturn(Optional.of(USER));
        userService.create(USERNAME, EMAIL, PASSWORD, BASE_URL);
    }

    @Test(expected = UsernameTakenException.class)
    public void testCreateWithExistingUsername() throws EmailTakenException, UsernameTakenException {
        when(mockDao.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(mockDao.findByUsername(USERNAME)).thenReturn(Collections.singletonList(USER));
        userService.create(USERNAME, EMAIL, PASSWORD, BASE_URL);
    }

    @Test
    public void testCreateWithNewUser() throws EmailTakenException, UsernameTakenException {
        when(mockDao.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(mockDao.findByUsername(USERNAME)).thenReturn(Collections.emptyList());
        when(encoder.encode(PASSWORD)).thenReturn(PASSWORD);
        when(mockDao.create(USERNAME, EMAIL, encoder.encode(PASSWORD))).thenReturn(USER);

        Optional<User> result = userService.create(USERNAME, EMAIL, PASSWORD, BASE_URL);

        assertTrue(result.isPresent());
        assertEquals(USER, result.get());
    }

//    UPDATE USER
    @Test
    public void testUpdateWithEmptyFields() throws UsernameTakenException, IncorrectPasswordException {
        when(mockDao.update(USER, USERNAME, PASSWORD)).thenReturn(Optional.of(USER));
        when(encoder.matches(PASSWORD, PASSWORD)).thenReturn(true);
        when(encoder.encode(PASSWORD)).thenReturn(PASSWORD);
        when(mockDao.findByUsername(USERNAME)).thenReturn(Collections.singletonList(USER));
        when(mockDao.findByUsername(USERNAME)).thenReturn(Collections.singletonList(USER));

        Optional<User> result = userService.update(USER, "", "", PASSWORD);

        assertTrue(result.isPresent());
        assertEquals(USER, result.get());
    }

    @Test
    public void testUpdateJustUsername() throws UsernameTakenException, IncorrectPasswordException {
        String newUsername = "newUsername";
        User expectedResult = new User(USER.getId(), newUsername, USER.getEmail(), USER.getPassword());

        when(mockDao.update(USER, newUsername, PASSWORD)).thenReturn(Optional.of(expectedResult));
        when(encoder.matches(PASSWORD, PASSWORD)).thenReturn(true);
        when(encoder.encode(PASSWORD)).thenReturn(PASSWORD);

        Optional<User> result = userService.update(USER, newUsername, "", PASSWORD);

        assertTrue(result.isPresent());
        assertEquals(expectedResult, result.get());
    }

    @Test(expected = IncorrectPasswordException.class)
    public void testUpdateWithIncorrectPassword() throws UsernameTakenException, IncorrectPasswordException {
        userService.update(USER, USERNAME, PASSWORD, "wrongPassword");
    }

    @Test(expected = UsernameTakenException.class)
    public void testUpdateWithExistingUsername() throws UsernameTakenException, IncorrectPasswordException {
        List<User> users = Arrays.asList(USER, new User(2L, USERNAME, "diffemail@example.com", PASSWORD));

        when(mockDao.findByUsername(USERNAME)).thenReturn(users);
        when(encoder.matches(PASSWORD, PASSWORD)).thenReturn(true);

        userService.update(USER, USERNAME, PASSWORD, PASSWORD);
    }

    @Test
    public void testUpdateWithNewUser() throws UsernameTakenException, IncorrectPasswordException {
        when(mockDao.findByUsername(USERNAME)).thenReturn(Collections.emptyList());
        when(mockDao.update(USER, USERNAME, PASSWORD)).thenReturn(Optional.of(USER));
        when(encoder.matches(PASSWORD, PASSWORD)).thenReturn(true);
        when(encoder.encode(PASSWORD)).thenReturn(PASSWORD);


        Optional<User> result = userService.update(USER, USERNAME, PASSWORD, PASSWORD);

        assertTrue(result.isPresent());
        assertEquals(USER, result.get());
    }

}
