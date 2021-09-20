package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistance.ForumDao;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Forum;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
@Repository
public class ForumJdbcDao implements ForumDao {

    private final JdbcTemplate jdbcTemplate;

    private final static RowMapper<Forum> ROW_MAPPER = (rs, rowNum) ->
            new Forum(rs.getLong("forum_id"),
                    rs.getString("name"),
                    new Community(
                            rs.getLong("community_id" ),
                            rs.getString("community_name"),
                            rs.getString("description"),
                            new User(rs.getLong("moderator_id"), rs.getString("user_name"), rs.getString("user_email"))));

    @Autowired
    public ForumJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Override
    public List<Forum> list(){
        return jdbcTemplate.query(
                "SELECT forum.forum_id, " +
                "forum.name, " +
                "community.community_id, " +
                "community.name, " +
                "community.description, " +
                "users.username AS user_name, users.email AS user_email " +
                "FROM forum JOIN community ON forum.community_id = community.community_id "+
                "JOIN users ON community.moderator_id = users.user_id", ROW_MAPPER);
    }


    @Override
    public List<Forum> findByCommunity(Number communityId){
        return jdbcTemplate.query("SELECT forum_id, f.name, f.community_id, c.name as community_name, c.description, users.username AS user_name, users.email AS user_email FROM forum f join community c on f.community_id = c.community_id JOIN users ON community.moderator_id = users.user_id where f.community_id = ?" , ROW_MAPPER , communityId.longValue());
    }


}
