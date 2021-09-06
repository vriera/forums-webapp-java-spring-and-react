import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Forum;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.QuestionJdbcDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;

import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Sql("classpath:schema.sql")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class QuestionJdbcDaoTest {
	@Autowired
	private DataSource ds;

	@Autowired
	private QuestionJdbcDao questionJdbcDao;

	JdbcTemplate jdbcTemplate;

	final String TITLE = "Test Title";
	final String BODY = "Test Body";
	final String USERNAME = "test_user";
	final String EMAIL = "email@example.com";
	final String COMMUNITY = "Test Community";
	final String FORUM = "Test Forum";

	@Before
	public void setUp() {
		jdbcTemplate = new JdbcTemplate(ds);
		JdbcTestUtils.deleteFromTables(jdbcTemplate, "question", "users", "community", "forum");
		Statement statement;
		try {
			statement = ds.getConnection().createStatement();
			statement.execute("INSERT INTO users(username, email) VALUES ('test_user', 'email@example.com')");
			statement.execute("INSERT INTO community(name) VALUES ('Test Community')");
			statement.execute("INSERT INTO forum(name, community_id) VALUES ('Test Forum', (SELECT community_id FROM community c where c.name = 'Test Community'))");
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testCreate(){
		final User owner = new User(1, USERNAME, EMAIL);
		final Community community = new Community(1, COMMUNITY);
		final Forum forum = new Forum(1, FORUM, community);

		//final Question question = questionJdbcDao.create(TITLE, BODY, owner, forum);

		/*assertNotNull(question);
		assertEquals(TITLE, question.getTitle());
		assertEquals(BODY, question.getBody());
		assertEquals(owner.getId(), question.getOwner().getId());
		assertEquals(forum.getId(), question.getForum().getId());
		assertEquals(community.getId(), question.getCommunity().getId());
		assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, "question"));*/
	}
}
