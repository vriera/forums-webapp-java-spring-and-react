package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistance.CommunityDao;
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
            rs.getString("description"));

    @Autowired
    public CommunityJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("community")
                .usingGeneratedKeyColumns("community_id");
    }

    @Override
    public  Optional<Community> findById(Number id ){ return jdbcTemplate.query("SELECT * FROM community where community_id = ?" , ROW_MAPPER , id.longValue()).stream().findFirst();};

    @Override
    public List<Community> list(){
        return jdbcTemplate.query("SELECT * FROM community" , ROW_MAPPER);
    };

    @Override
    public Community create(String name, String description, User moderator){
        final Map<String, Object> args = new HashMap<>();
        args.put("name", name);
        args.put("description", description);
        final Map<String, Object> keys = jdbcInsert.executeAndReturnKeyHolder(args).getKeys();
        Long id = ((Integer) keys.get("community_id")).longValue();
        return new Community(id, name, description);
    }
}
