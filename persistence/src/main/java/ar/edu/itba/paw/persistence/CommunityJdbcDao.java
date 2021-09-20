package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistance.CommunityDao;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.*;

@Repository
public class CommunityJdbcDao implements CommunityDao {


    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final static RowMapper<Community> ROW_MAPPER = (rs, rowNum) -> new Community(rs.getLong("community_id"),
            rs.getString("name"),
            rs.getString("description"),
            new User(rs.getLong("moderator_id"), rs.getString("user_name"), rs.getString("user_email")));


    private final String MAPPED_QUERY = "SELECT community_id, name, description, moderator_id, username AS user_name, email AS user_email FROM community JOIN users on community.moderator_id = user_id";

    @Autowired
    public CommunityJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("community")
                .usingGeneratedKeyColumns("community_id");
    }

    @Override
    public  Optional<Community> findById(Number id ){ return jdbcTemplate.query(MAPPED_QUERY + " where community_id = ?" , ROW_MAPPER , id.longValue()).stream().findFirst();};

    @Override
    public List<Community> list(){
        return jdbcTemplate.query(MAPPED_QUERY , ROW_MAPPER);
    };

    @Override
    public Community create(String name, String description, User moderator){
        final Map<String, Object> args = new HashMap<>();
        args.put("name", name);
        args.put("description", description);
        args.put("moderator_id", moderator.getId());
        final Map<String, Object> keys = jdbcInsert.executeAndReturnKeyHolder(args).getKeys();
        Long id = ((Integer) keys.get("community_id")).longValue();
        return new Community(id, name, description, moderator);
    }
}
