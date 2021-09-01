package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistance.ForumDao;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Forum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
@Repository
public class ForumJdbcDao implements ForumDao {

    private JdbcTemplate jdbcTemplate;

    private final static RowMapper<Forum> ROW_MAPPER = (rs, rowNum) ->
            new Forum(rs.getString("name") ,
                    rs.getLong("id") ,
                    new Community(
                            rs.getLong("community_id" ),
                            rs.getString("community_name")));

    @Autowired
    public ForumJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Override
    public List<Forum> list(){
        return jdbcTemplate.query("SELECT forum.id as id , " +
                "forum.name as name, " +
                "community.id as community_id," +
                "community.name as community_name FROM forum join community on forum.community_id = community.id" , ROW_MAPPER);
    };


    @Override
    public List<Forum> findByCommunity( Community community){
        RowMapper<Forum> mapper = (rs, rowNum) -> new Forum( rs.getString("name"), rs.getLong("id")  , community);
        return jdbcTemplate.query("SELECT * FROM forum where community_id = ?" ,mapper , community.getId());
    };


}
