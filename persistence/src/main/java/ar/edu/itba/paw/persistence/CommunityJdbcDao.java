package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistance.CommunityDao;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class CommunityJdbcDao implements CommunityDao {


    private JdbcTemplate jdbcTemplate;

    private final static RowMapper<Community> ROW_MAPPER = (rs, rowNum) -> new Community(rs.getLong("id") , rs.getString("name"));

    @Autowired
    public CommunityJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Override
    public List<Community> list(){
        return jdbcTemplate.query("SELECT * FROM community" , ROW_MAPPER);
    };
}
