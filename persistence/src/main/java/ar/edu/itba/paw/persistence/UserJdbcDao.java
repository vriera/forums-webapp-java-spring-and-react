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
    private final static RowMapper<User> ROW_MAPPER = (rs, rowNum) -> new User(rs.getString("username"), rs.getString("password"), rs.getLong("userid"));

    @Autowired
    public UserJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("userid");
        jdbcTemplate.execute(
                "CREATE TABLE IF NOT EXISTS users ("
                + "userid SERIAL PRIMARY KEY,"
                + "username varchar(100),"
                + "password varchar(100)"
                + ")");

    }


    @Override
    public List<User> list() {
        return null;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        final List<User> list = jdbcTemplate.query("SELECT * FROM users WHERE username = ?", ROW_MAPPER, username);
        return Optional.ofNullable(list.get(0));
    }

    @Override
    public Optional<User> findById(final long id) {
        final List<User> list = jdbcTemplate.query("SELECT * FROM users WHERE userid = ?", ROW_MAPPER, id);
        return Optional.ofNullable(list.get(0));
    }

    @Override
    public User create(final String username, final String password) {
        final Map<String, Object> args = new HashMap<>();
        args.put("username", username); // la key es el nombre de la columna
        args.put("password", password); 
        final Number userId = jdbcInsert.executeAndReturnKey(args);
        return new User(username, password, userId.longValue());
    }
}
