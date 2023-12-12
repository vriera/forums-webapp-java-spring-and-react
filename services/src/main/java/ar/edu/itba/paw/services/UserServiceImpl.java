package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistance.AnswersDao;
import ar.edu.itba.paw.interfaces.persistance.CommunityDao;
import ar.edu.itba.paw.interfaces.persistance.QuestionDao;
import ar.edu.itba.paw.interfaces.persistance.UserDao;
import ar.edu.itba.paw.interfaces.services.MailingService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.exceptions.EmailAlreadyExistsException;
import ar.edu.itba.paw.models.exceptions.IncorrectPasswordException;
import ar.edu.itba.paw.models.exceptions.UsernameAlreadyExistsException;
import ar.edu.itba.paw.services.utils.PaginationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private static final int PAGE_SIZE = PaginationUtils.PAGE_SIZE;

    @Autowired
    private UserDao userDao;

    @Autowired
    private CommunityDao communityDao;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private AnswersDao answersDao;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private MailingService mailingService;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public User update(User user, String newUsername, String newPassword, String currentPassword) {

        // If fields are empty, do not update
        newPassword = (newPassword == null || newPassword.isEmpty()) ? user.getPassword() : encoder.encode(newPassword);
        newUsername = (newUsername == null || newUsername.isEmpty()) ? user.getUsername() : newUsername;

        if (!encoder.matches(currentPassword, user.getPassword())) {
            LOGGER.error("[UPDATE USER] Incorrect password for user with id: {}", user.getId());
            throw new IncorrectPasswordException();
        }

        List<User> usersWithDesiredUsername = userDao.findByUsername(newUsername);
        boolean otherUserHasDesiredUsername = usersWithDesiredUsername.stream()
                .anyMatch(u -> u.getId() != user.getId());
        if (otherUserHasDesiredUsername)
            throw new UsernameAlreadyExistsException();

        LOGGER.debug("[UPDATE USER]: username: {}, password: {}", newUsername, newPassword);
        return userDao.update(user, newUsername, encoder.encode(newPassword)).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public User findById(long id) {
        if (id < 0)
            throw new IllegalArgumentException();

        return userDao.findById(id).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public User findByEmail(String email) {
        if ( email == null || email.isEmpty())
            throw new IllegalArgumentException();

        return userDao.findByEmail(email).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public User create(final String username, final String email, String password, String baseUrl) {

        boolean fieldsAreValid = username != null && !username.isEmpty() && email != null && !email.isEmpty()
                && password != null && !password.isEmpty();

        if (!fieldsAreValid) {
            //TODO: Better error
            throw new IllegalArgumentException();
        }

        try {
            User userInDatabase = this.findByEmail(email);

            LOGGER.debug("[CREATE USER] User with email: {} already exists - id: {}, username: {}, password: {}",
                    userInDatabase.getEmail(), userInDatabase.getId(), userInDatabase.getUsername(),
                    userInDatabase.getPassword());

            return handleExistingUser(userInDatabase, password, username);
        } catch (NoSuchElementException e) {

            return handleNewUser(username, email, password, baseUrl);
        }

    }

    private User handleExistingUser(User user, String password, String username) {
        // If existing user has a password, it means it is not a guest account and the
        // email is taken

        LOGGER.debug("[CREATE USER] Handling user with email: {} - already exists", user.getEmail());
        if (!(user.getPassword() == null || user.getPassword().isEmpty())) throw new EmailAlreadyExistsException();
        

        LOGGER.debug("[CREATE USER] Attempting to override user with username: {}, password: {}", username, password);
        return userDao.update(user, username, encoder.encode(password)).orElseThrow(NoSuchElementException::new);
    }

    private User handleNewUser(String username, String email, String password, String baseUrl) {
        if (isUsernameTaken(username)) {
            throw new UsernameAlreadyExistsException();
        }

        User createdUser = userDao.create(username, email, encoder.encode(password));

        sendUserCreationEmail(createdUser, baseUrl);
        return createdUser;
    }

    private boolean isUsernameTaken(String username) {
        List<User> usersWithDesiredUsername = userDao.findByUsername(username);
        return !usersWithDesiredUsername.isEmpty();
    }

    private void sendUserCreationEmail(User user, String baseUrl) {
        mailingService.verifyEmail(user.getEmail(), user, baseUrl, LocaleContextHolder.getLocale());
    }

    private Community addUserCount(Community c) {
        Number count = communityDao.getUserCount(c.getId()).orElse(0L);
        c.setUserCount(count.longValue());
        return c;
    }

    @Override
    public List<Community> getCommunitiesByAccessType(Number userId, AccessType type, Number page) {
        if (userId.longValue() < 0)
            return Collections.emptyList();

        return communityDao.getCommunitiesByAccessType(userId, type, page.longValue() * PAGE_SIZE, PAGE_SIZE).stream()
                .map(this::addUserCount).collect(Collectors.toList());
    }

    @Override
    public long getCommunitiesByAccessTypePagesCount(Number userId, AccessType type) {
        if (userId == null || userId.longValue() < 0)
            return -1;

        long total = communityDao.getCommunitiesByAccessTypeCount(userId, type);
        return PaginationUtils.getPagesFromTotal(total);
    }

    /*
     * Questions by user
     */
    @Override
    public List<Question> getQuestions(Number id, Number page) {
        boolean idIsInvalid = id == null || id.longValue() < 0;
        boolean pageIsInvalid = page == null || page.intValue() < 0;
        if (idIsInvalid || pageIsInvalid)
            return Collections.emptyList();

        return questionDao.findByUser(id.longValue(), page.intValue() * PAGE_SIZE, PAGE_SIZE);
    }

    @Override
    public long getQuestionsPagesCount(Number id) {
        if (id.longValue() <= 0)
            throw new IllegalArgumentException("Id must be over 0");
        return PaginationUtils.getPagesFromTotal(questionDao.findByUserCount(id.longValue()));
    }

    /*
     * Answers by user
     */
    @Override
    public List<Answer> getAnswers(Long userid, int page) {
        if (userid < 1)
            return Collections.emptyList();
        return answersDao.findByUser(userid, PAGE_SIZE, PAGE_SIZE * page);
    }

    @Override
    public long getAnswersPagesCount(Long userId) {
        if (userId < 1)
            throw new IllegalArgumentException("Id must be over 0");

        Optional<Long> countByUser = answersDao.findByUserCount(userId);

        if (!countByUser.isPresent())
            throw new NoSuchElementException("");

        return PaginationUtils.getPagesFromTotal(countByUser.get().intValue());
    }

    /*
     * Notifications
     */
    @Override
    public Notification getNotifications(Number userId) {
        return userDao.getNotifications(userId).orElseThrow(NoSuchElementException::new);
    }

    /*
     * Karma
     */

    @Override
    public Karma getKarma(Number userId) {
        return userDao.getKarma(userId).orElseThrow(NoSuchElementException::new);
    }

}
