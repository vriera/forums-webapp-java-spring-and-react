package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistance.UserDao;
import ar.edu.itba.paw.interfaces.services.MailingService;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.exceptions.EmailAlreadyExistsException;
import ar.edu.itba.paw.models.exceptions.IncorrectPasswordException;
import ar.edu.itba.paw.models.exceptions.UsernameAlreadyExistsException;
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

//    @Test
//    public void testCreateWithEmptyFields() throws EmailAlreadyExistsException, UsernameAlreadyExistsException {
//        Optional<User> result = userService.create("", "", "", BASE_URL);
//        assertFalse(result.isPresent());
//    }
//
//    @Test(expected = EmailAlreadyExistsException.class)
//    public void testCreateWithExistingUser() throws EmailAlreadyExistsException, UsernameAlreadyExistsException {
//        when(mockDao.findByEmail(EMAIL)).thenReturn(Optional.of(USER));
//        userService.create(USERNAME, EMAIL, PASSWORD, BASE_URL);
//    }
//
//    @Test(expected = UsernameAlreadyExistsException.class)
//    public void testCreateWithExistingUsername() throws EmailAlreadyExistsException, UsernameAlreadyExistsException {
//        when(mockDao.findByEmail(EMAIL)).thenReturn(Optional.empty());
//        when(mockDao.findByUsername(USERNAME)).thenReturn(Collections.singletonList(USER));
//        userService.create(USERNAME, EMAIL, PASSWORD, BASE_URL);
//    }
//
//    @Test
//    public void testCreateWithNewUser() throws EmailAlreadyExistsException, UsernameAlreadyExistsException {
//        when(mockDao.findByEmail(EMAIL)).thenReturn(Optional.empty());
//        when(mockDao.findByUsername(USERNAME)).thenReturn(Collections.emptyList());
//        when(encoder.encode(PASSWORD)).thenReturn(PASSWORD);
//        when(mockDao.create(USERNAME, EMAIL, encoder.encode(PASSWORD))).thenReturn(USER);
//
//        Optional<User> result = userService.create(USERNAME, EMAIL, PASSWORD, BASE_URL);
//
//        assertTrue(result.isPresent());
//        assertEquals(USER, result.get());
//    }
//
////    UPDATE USER
//    @Test
//    public void testUpdateWithEmptyFields() throws UsernameAlreadyExistsException, IncorrectPasswordException {
//        when(mockDao.update(USER, USERNAME, PASSWORD)).thenReturn(Optional.of(USER));
//        when(encoder.matches(PASSWORD, PASSWORD)).thenReturn(true);
//        when(encoder.encode(PASSWORD)).thenReturn(PASSWORD);
//        when(mockDao.findByUsername(USERNAME)).thenReturn(Collections.singletonList(USER));
//        when(mockDao.findByUsername(USERNAME)).thenReturn(Collections.singletonList(USER));
//
//        Optional<User> result = userService.update(USER, "", "", PASSWORD);
//
//        assertTrue(result.isPresent());
//        assertEquals(USER, result.get());
//    }
//
//    @Test
//    public void testUpdateJustUsername() throws UsernameAlreadyExistsException, IncorrectPasswordException {
//        String newUsername = "newUsername";
//        User expectedResult = new User(USER.getId(), newUsername, USER.getEmail(), USER.getPassword());
//
//        when(mockDao.update(USER, newUsername, PASSWORD)).thenReturn(Optional.of(expectedResult));
//        when(encoder.matches(PASSWORD, PASSWORD)).thenReturn(true);
//        when(encoder.encode(PASSWORD)).thenReturn(PASSWORD);
//
//        Optional<User> result = userService.update(USER, newUsername, "", PASSWORD);
//
//        assertTrue(result.isPresent());
//        assertEquals(expectedResult, result.get());
//    }
//
//    @Test(expected = IncorrectPasswordException.class)
//    public void testUpdateWithIncorrectPassword() throws UsernameAlreadyExistsException, IncorrectPasswordException {
//        userService.update(USER, USERNAME, PASSWORD, "wrongPassword");
//    }
//
//    @Test(expected = UsernameAlreadyExistsException.class)
//    public void testUpdateWithExistingUsername() throws UsernameAlreadyExistsException, IncorrectPasswordException {
//        List<User> users = Arrays.asList(USER, new User(2L, USERNAME, "diffemail@example.com", PASSWORD));
//
//        when(mockDao.findByUsername(USERNAME)).thenReturn(users);
//        when(encoder.matches(PASSWORD, PASSWORD)).thenReturn(true);
//
//        userService.update(USER, USERNAME, PASSWORD, PASSWORD);
//    }
//
//    @Test
//    public void testUpdateWithNewUser() throws UsernameAlreadyExistsException, IncorrectPasswordException {
//        when(mockDao.findByUsername(USERNAME)).thenReturn(Collections.emptyList());
//        when(mockDao.update(USER, USERNAME, PASSWORD)).thenReturn(Optional.of(USER));
//        when(encoder.matches(PASSWORD, PASSWORD)).thenReturn(true);
//        when(encoder.encode(PASSWORD)).thenReturn(PASSWORD);
//
//
//        Optional<User> result = userService.update(USER, USERNAME, PASSWORD, PASSWORD);
//
//        assertTrue(result.isPresent());
//        assertEquals(USER, result.get());
//    }

}
