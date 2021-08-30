package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistance.UserDao;
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

    private JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final static RowMapper<User> ROW_MAPPER = (rs, rowNum) -> new User(rs.getString("username"), rs.getString("password"), rs.getLong("id"));

    @Autowired
    public UserJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
    }


    @Override
    public List<User> list() {
        return null;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        final List<User> list = jdbcTemplate.query("SELECT * FROM users WHERE email = ?", ROW_MAPPER, email);
        return Optional.ofNullable(list.stream().findFirst().orElse(null));
    }

    @Override
    public User create(final String username, final String email) {
        final Map<String, Object> args = new HashMap<>();
        args.put("username", username); // la key es el nombre de la columna
        args.put("email", email);
        final Number userId = jdbcInsert.executeAndReturnKey(args);
        return new User(username, email, userId.longValue());
    }
}
