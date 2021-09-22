package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistance.ForumDao;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Forum;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Repository
public class ForumJdbcDao implements ForumDao {

    private final JdbcTemplate jdbcTemplate;

    private final static RowMapper<Forum> ROW_MAPPER = (rs, rowNum) ->
            new Forum(rs.getLong("forum_id"),
                    rs.getString("forum_name"),
                    new Community(
                            rs.getLong("community_id" ),
                            rs.getString("community_name"),
                            rs.getString("description"),
                                new User(rs.getLong("moderator_id"), rs.getString("user_name"), rs.getString("user_email"), rs.getString("password"))));

    private final String MAPPED_QUERY =
            "SELECT forum.forum_id as forum_id, forum.name as forum_name, community.community_id as community_id, community.name as community_name, community.description, community.moderator_id, " +
            "users.username AS user_name, users.email AS user_email, users.password " +
            "FROM forum JOIN community on forum.community_id = community.community_id " +
            "JOIN users ON community.moderator_id = users.user_id ";

    @Autowired
    public ForumJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Override
    public List<Forum> list(){
        return jdbcTemplate.query(
                MAPPED_QUERY , ROW_MAPPER);
    }


    @Override
    public List<Forum> findByCommunity(Number communityId){
        return jdbcTemplate.query(
                MAPPED_QUERY + "where community.community_id = ?" , ROW_MAPPER , communityId.longValue());
    }

    @Override
    public Optional<Forum> findById(Number forumId) {
        final List<Forum> list = jdbcTemplate.query(
                MAPPED_QUERY + "WHERE forum_id = ?", ROW_MAPPER, forumId.longValue());
        return list.stream().findFirst();
    }

}
