package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistance.AnswersDao;
import ar.edu.itba.paw.interfaces.persistance.CommunityDao;
import ar.edu.itba.paw.interfaces.persistance.QuestionDao;
import ar.edu.itba.paw.interfaces.persistance.UserDao;
import ar.edu.itba.paw.interfaces.services.MailingService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.exceptions.EmailTakenException;
import ar.edu.itba.paw.models.exceptions.IncorrectPasswordException;
import ar.edu.itba.paw.models.exceptions.UsernameTakenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
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

    private static final int PAGE_SIZE = 5;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public Optional<User> update(User user, String newUsername, String newPassword, String currentPassword) throws UsernameTakenException, IncorrectPasswordException {
        LOGGER.info("[UPDATE USER] id: {}, username: {}, password = {} => newUsername: {}, newPassword: {}, currentPassword: {}",
                user.getId(), user.getUsername(), user.getPassword(), newUsername, newPassword, currentPassword);

        // If fields are empty, do not update
        newPassword = (newPassword == null || newPassword.isEmpty()) ? user.getPassword() : encoder.encode(newPassword);
        newUsername = (newUsername == null || newUsername.isEmpty()) ? user.getUsername() : newUsername;
        LOGGER.info("[UPDATE USER REVISED] id: {}, username: {}, password = {} => newUsername: {}, newPassword: {}, currentPassword: {}",
                user.getId(), user.getUsername(), user.getPassword(), newUsername, newPassword, currentPassword);

        if (!encoder.matches(currentPassword, user.getPassword())) {
            LOGGER.error("[UPDATE USER] Incorrect password for user with id: {}", user.getId());
            throw new IncorrectPasswordException();
        }

        List<User> usersWithDesiredUsername = userDao.findByUsername(newUsername);
        boolean otherUserHasDesiredUsername = usersWithDesiredUsername.stream().anyMatch(u -> u.getId() != user.getId());
        if (otherUserHasDesiredUsername)
            throw new UsernameTakenException();

        LOGGER.debug("PERSISTING: username: {}, password: {}", newUsername, newPassword);
        return userDao.update(user, newUsername, newPassword);
    }

    @Override
    public Optional<User> findById(long id) {
        if (id < 0)
            return Optional.empty();

        return userDao.findById(id);
    }

    @Override
    public List<User> list() {
        return this.userDao.list();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (email.isEmpty())
            return Optional.empty();

        return userDao.findByEmail(email);
    }

    @Override
    public Optional<User> create(final String username, final String email, String password, String baseUrl) throws UsernameTakenException, EmailTakenException {
        boolean fieldsAreValid = username != null && !username.isEmpty() && email != null && !email.isEmpty() && password != null && !password.isEmpty();
        if (!fieldsAreValid) {
            return Optional.empty();
        }

        Optional<User> userInDatabase = this.findByEmail(email);
        if (userInDatabase.isPresent()) {
            return handleExistingUser(userInDatabase.get(), password, username);
        } else {
            return handleNewUser(username, email, password, baseUrl);
        }
    }

    private Optional<User> handleExistingUser(User user, String password, String username) throws EmailTakenException, UsernameTakenException {
        // If existing user has a password, it means it is not a guest account and the email is taken
        if (user.getPassword() != null || !user.getPassword().isEmpty()) {
            throw new EmailTakenException();
        }

        return overwriteGuestAccount(user, password, username);
    }

    private Optional<User> overwriteGuestAccount(User user, String password, String username) throws UsernameTakenException {
        try {
            Optional<User> createdUser = this.update(user, username, password, user.getPassword());
            LOGGER.info("[CREATE USER] Overwrote guest account with id: {}, username: {}", user.getId(), user.getUsername());
            return createdUser;
        } catch (IncorrectPasswordException e) {
            LOGGER.error("[CREATE USER] Incorrect password for user with id: {}", user.getId());
            return Optional.empty();
        }
    }

    private Optional<User> handleNewUser(String username, String email, String password, String baseUrl) throws UsernameTakenException {
        if (isUsernameTaken(username)) {
            throw new UsernameTakenException();
        }

        Optional<User> createdUser = Optional.of(userDao.create(username, email, encoder.encode(password)));
        sendUserCreationEmail(createdUser, baseUrl);
        return createdUser;
    }

    private boolean isUsernameTaken(String username) {
        List<User> usersWithDesiredUsername = userDao.findByUsername(username);
        return !usersWithDesiredUsername.isEmpty();
    }

    private void sendUserCreationEmail(Optional<User> u, String baseUrl) {
        u.ifPresent(user -> mailingService.verifyEmail(user.getEmail(), user, baseUrl, LocaleContextHolder.getLocale()));
    }


    @Override
    public List<Community> getModeratedCommunities(Number id, Number page) {
        if (id.longValue() < 0 || page.intValue() < 0)
            return Collections.emptyList();

        List<Community> cList = communityDao.getByModerator(id, page.intValue() * PAGE_SIZE, PAGE_SIZE);
        for (Community c : cList) {
            addUserCount(c);
            Optional<CommunityNotifications> notifications = communityDao.getCommunityNotificationsById(c.getId());
            if (notifications.isPresent()) {
                c.setNotifications(notifications.get().getNotifications());
            } else {
                c.setNotifications(0L);
            }
        }
        return cList;
    }


    @Override
    public long getModeratedCommunitiesPages(Number id) {
        if (id == null || id.longValue() < 0)
            return -1;

        long total = communityDao.getByModeratorCount(id);
        return (total % PAGE_SIZE == 0) ? total / PAGE_SIZE : (total / PAGE_SIZE) + 1;
    }

    @Override
    public boolean isModerator(Number id, Number communityId) {
        long pages = getModeratedCommunitiesPages(id);
        for (int i = 0; i < pages; i++) {
            List<Community> cl = getModeratedCommunities(id, i);
            for (Community c : cl) {
                if (c.getId() == communityId.longValue()) {
                    return true;
                }
            }
        }
        return false;
    }

    private Community addUserCount(Community c) {
        Number count = communityDao.getUserCount(c.getId()).orElse(0);
        c.setUserCount(count.longValue());
        return c;
    }

    @Override
    public List<Community> getCommunitiesByAccessType(Number userId, AccessType type, Number page) {
        if (userId.longValue() < 0)
            return Collections.emptyList();

        return communityDao.getCommunitiesByAccessType(userId, type, page.longValue() * PAGE_SIZE, PAGE_SIZE).stream().map(this::addUserCount).collect(Collectors.toList());
    }

    @Override
    public long getCommunitiesByAccessTypePages(Number userId, AccessType type) {
        if (userId == null || userId.longValue() < 0)
            return -1;

        long total = communityDao.getCommunitiesByAccessTypeCount(userId, type);
        return (total % PAGE_SIZE == 0) ? total / PAGE_SIZE : (total / PAGE_SIZE) + 1;
    }

    @Override
    public List<Question> getQuestions(Number id, Number page) {
        boolean idIsInvalid = id == null || id.longValue() < 0;
        boolean pageIsInvalid = page == null || page.intValue() < 0;
        if (idIsInvalid || pageIsInvalid)
            return Collections.emptyList();

        return questionDao.findByUser(id.longValue(), page.intValue() * PAGE_SIZE, PAGE_SIZE);
    }

    @Override
    public int getPageAmountForQuestions(Number id) {
        if (id.longValue() < 0)
            return -1;
        int count = questionDao.findByUserCount(id.longValue());
        int mod = count % PAGE_SIZE;
        return mod != 0 ? (count / PAGE_SIZE) + 1 : count / PAGE_SIZE;
    }

    @Override
    public List<Answer> getAnswers(Number id, Number page) {
        if (id.longValue() < 0)
            return Collections.emptyList();

        return answersDao.findByUser(id.longValue(), page.intValue() * PAGE_SIZE, PAGE_SIZE);
    }

    @Override
    public int getPageAmountForAnswers(Number id) {
        if (id.longValue() < 0) {
            return -1;
        }
        Optional<Long> countByUser = answersDao.findByUserCount(id.longValue());
        if (!countByUser.isPresent()) {
            return -1;
        }

        int count = countByUser.get().intValue();
        int mod = count % PAGE_SIZE;

        return mod != 0 ? (count / PAGE_SIZE) + 1 : count / PAGE_SIZE;
    }

    @Override
    public Optional<AccessType> getAccess(Number userId, Number communityId) {
        if (userId == null || userId.longValue() < 0 || communityId == null || communityId.longValue() < 0)
            return Optional.empty();
        return communityDao.getAccess(userId, communityId);
    }

    @Override
    public Optional<Notification> getNotifications(Number userId) {
        return userDao.getNotifications(userId);
    }

    @Override
    public Optional<Karma> getKarma(Number userId) {
        return userDao.getKarma(userId);
    }

    @Override
    public List<User> getUsers(int page) {
        return userDao.getUsers(page);
    }

}
