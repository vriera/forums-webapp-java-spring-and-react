import ar.edu.itba.paw.models.AccessType;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.CommunityJdbcDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;

@Sql("classpath:schema.sql")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class CommunityJdbcDaoTest {
	@Autowired
	private DataSource ds;

	@Autowired
	private CommunityJdbcDao communityJdbcDao;

	JdbcTemplate jdbcTemplate;
	SimpleJdbcInsert userJdbcInsert;
	SimpleJdbcInsert communityJdbcInsert;
	SimpleJdbcInsert accessJdbcInsert;

	long COMMUNITY_ID;
	private final String NAME = "SAMPLE COMMUNITY";
	private final String DESC = "THIS IS A SAMPLE COMMUNITY";

	long MOD_ID;
	private final String USERNAME = "USER";
	private final String EMAIL = "example@email.com";
	private final String PASSWORD = "password";

	private final String USER_EMAIL = "example+1@email.com";

	@Before
	public void setUp() {

		jdbcTemplate = new JdbcTemplate(ds);

		JdbcTestUtils.deleteFromTables(jdbcTemplate, "access", "community", "users");

		userJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
				.withTableName("users")
				.usingGeneratedKeyColumns("user_id");

		communityJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
				.withTableName("community")
				.usingGeneratedKeyColumns("community_id");

		accessJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
				.withTableName("access")
				.usingGeneratedKeyColumns("access_id");

		final Map<String, Object> modArgs = new HashMap<>();
		modArgs.put("username", USERNAME);
		modArgs.put("email", EMAIL);
		modArgs.put("password", PASSWORD);
		MOD_ID = userJdbcInsert.executeAndReturnKey(modArgs).longValue();


		final Map<String, Object> communityArgs = new HashMap<>();
		communityArgs.put("name", NAME);
		communityArgs.put("description", DESC);
		communityArgs.put("moderator_id", MOD_ID);
		COMMUNITY_ID = communityJdbcInsert.executeAndReturnKey(communityArgs).longValue();
	}

	private Number insertAccess(User user, AccessType type){
		final Map<String, Object> accessArgs = new HashMap<>();
		accessArgs.put("user_id", user.getId());
		accessArgs.put("community_id", COMMUNITY_ID);
		accessArgs.put("access_type", type.ordinal());
		return accessJdbcInsert.executeAndReturnKey(accessArgs);
	}

	private Number insertUser(){
		final Map<String, Object> args = new HashMap<>();
		args.put("username", USERNAME);
		args.put("email", USER_EMAIL);
		args.put("password", PASSWORD);
		return userJdbcInsert.executeAndReturnKey(args);
	}

	private Optional<AccessType> checkAccess(Number userId, Number communityId){
		RowMapper<AccessType> ACCESS_ROW_MAPPER = (rs, rowNum) -> AccessType.valueOf(rs.getInt("access_type"));
		return jdbcTemplate.query("SELECT * FROM access WHERE user_id = ? and community_id = ?", ACCESS_ROW_MAPPER, userId.longValue(), communityId.longValue()).stream().findFirst();
	}

	@Test
	public void testNewAccess(){
		Number userId = insertUser();

		communityJdbcDao.updateAccess(userId, COMMUNITY_ID, AccessType.REQUESTED);

		assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, "access"));
	}

	@Test
	public void testRepeatedAccess(){
		Number userId = insertUser();

		communityJdbcDao.updateAccess(userId, COMMUNITY_ID, AccessType.REQUESTED);
		communityJdbcDao.updateAccess(userId, COMMUNITY_ID, AccessType.ADMITTED);

		Optional<AccessType> rsAccess = checkAccess(userId, COMMUNITY_ID);
		assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, "access"));
		assertTrue(rsAccess.isPresent());
		assertEquals(rsAccess.get(), AccessType.ADMITTED);
	}
	
	@Test
	public void testDeleteAccess(){
		Number userId = insertUser();

		communityJdbcDao.updateAccess(userId, COMMUNITY_ID, AccessType.ADMITTED);
		communityJdbcDao.updateAccess(userId, COMMUNITY_ID, null);

		assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, "access"));
	}

	@Test
	public void testDeleteNonexistentAccess(){
		Number userId = insertUser();

		communityJdbcDao.updateAccess(userId, COMMUNITY_ID, null);

		assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, "access"));
	}

}
