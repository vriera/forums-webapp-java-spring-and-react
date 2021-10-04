package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistance.UserDao;
import ar.edu.itba.paw.models.AccessType;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserJdbcDao implements UserDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final static RowMapper<User> ROW_MAPPER = (rs, rowNum) -> new User(rs.getLong("user_id"), rs.getString("username"), rs.getString("email"), rs.getString("password"));

    @Autowired
    public UserJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
    }


    @Override
    public List<User> list() {
        return jdbcTemplate.query("SELECT * FROM users", ROW_MAPPER);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        final List<User> list = jdbcTemplate.query("SELECT * FROM users WHERE email = ?", ROW_MAPPER, email);
        return list.stream().findFirst();
    }

    @Override
    public Optional<User> findById(long id){
        final List<User> list = jdbcTemplate.query("SELECT * FROM users WHERE user_id = ?", ROW_MAPPER, id);
        return list.stream().findFirst();
    }

    @Override
    public User create(final String username, final String email, final String password) {
        final Map<String, Object> args = new HashMap<>();
        args.put("username", username);
        args.put("email", email);
        args.put("password", password);
        final Number userId = jdbcInsert.executeAndReturnKey(args);
        return new User(userId.longValue(), username, email, password);
    }

    @Override
    public Optional<User> updateCredentials(Number id, String newUsername, String newPassword) {
        final List<User> list = jdbcTemplate.query("UPDATE users SET username = ?, password = ? WHERE user_id = ? RETURNING * ", ROW_MAPPER, newUsername, newPassword, id.longValue());
        return list.stream().findFirst();
    }



    @Override
    public List<User> getMembersByAccessType(Number communityId, AccessType type) {
        String ACCESS_MAPPED_QUERY = "SELECT users.user_id as user_id, users.username as username, users.email as email, users.password as password FROM access JOIN users on access.user_id = users.user_id ";
        if(type == null)
            return jdbcTemplate.query(ACCESS_MAPPED_QUERY + "WHERE community_id = ?", ROW_MAPPER, communityId.longValue());

        return jdbcTemplate.query(ACCESS_MAPPED_QUERY + "WHERE community_id = ? and access_type = ?", ROW_MAPPER, communityId);
    }
}
