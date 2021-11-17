/*import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.TestConfig;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Sql("classpath:schema.sql")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class UserJdbcDaoTest {
	private static final String PASSWORD = "password";
	private static final String USERNAME = "username";
	private static final String EMAIL = "email@example.com";
	@Autowired
	private DataSource ds;
	@Autowired
	private UserJdbcDao userDao;
	private JdbcTemplate jdbcTemplate;
	@Before
	public void setUp() {
		jdbcTemplate = new JdbcTemplate(ds);
		JdbcTestUtils.deleteFromTables(jdbcTemplate, "users");
	}
	@Test
	public void testCreate() {
		final User user = userDao.create(EMAIL, USERNAME, PASSWORD);
		assertNotNull(user);
		assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, "users"));
	}
}
*/