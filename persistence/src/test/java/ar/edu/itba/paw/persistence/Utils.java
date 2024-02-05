package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Utils {

    static List<User> TEST_USERS = Arrays.asList(
            new User(1L, "User 1", "user1@test.com", "password"),
            new User(2L, "User 2", "user2@test.com", "password"),
            new User(3L, "User 3", "user3@test.com", "password")
            );

    static List<Community> TEST_COMMUNITIES = Arrays.asList(
            new Community(1L, "Community 1", "Description 1", TEST_USERS.get(0)),
            new Community(2L, "Community 2", "Description 2", TEST_USERS.get(1)),
            new Community(3L, "Community 3", "Description 3", TEST_USERS.get(2))
    );

    static List<Forum> TEST_FORUMS = Arrays.asList(
            new Forum(1L, "Forum 1", TEST_COMMUNITIES.get(0)),
            new Forum(2L, "Forum 2", TEST_COMMUNITIES.get(1)),
            new Forum(3L, "Forum 3", TEST_COMMUNITIES.get(2))
    );

    static List<Question> TEST_QUESTIONS = Arrays.asList(
            new Question(1L, Date.from(Instant.now()), "Question 1", "Body 1", TEST_USERS.get(0), TEST_COMMUNITIES.get(0), TEST_FORUMS.get(0), null),
            new Question(2L, Date.from(Instant.now()), "Question 2", "Body 2", TEST_USERS.get(0), TEST_COMMUNITIES.get(0), TEST_FORUMS.get(0), null),
            new Question(3L, Date.from(Instant.now()), "Question 3", "Body 3", TEST_USERS.get(2), TEST_COMMUNITIES.get(2), TEST_FORUMS.get(2), null),
            new Question(4L, Date.from(Instant.now()), "Question 4", "Body 4", TEST_USERS.get(0), TEST_COMMUNITIES.get(0), TEST_FORUMS.get(0), null),
            new Question(5L, Date.from(Instant.now()), "Question 5", "Body 5", TEST_USERS.get(1), TEST_COMMUNITIES.get(1), TEST_FORUMS.get(1), null),
            new Question(6L, Date.from(Instant.now()), "Question 6", "Body 6", TEST_USERS.get(2), TEST_COMMUNITIES.get(2), TEST_FORUMS.get(2), null)
    );

    static List<Answer> TEST_ANSWERS = Arrays.asList(
            new Answer(1l, "Answer 1", true, TEST_QUESTIONS.get(0), TEST_USERS.get(0), Date.from(Instant.now())),
            new Answer(2l, "Answer 2", false, TEST_QUESTIONS.get(0), TEST_USERS.get(2), Date.from(Instant.now())),
            new Answer(3l, "Answer 3", true, TEST_QUESTIONS.get(1), TEST_USERS.get(2), Date.from(Instant.now())),
            new Answer(4l, "Answer 4", true, TEST_QUESTIONS.get(2), TEST_USERS.get(0), Date.from(Instant.now()))
    );
}
