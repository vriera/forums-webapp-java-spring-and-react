package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistance.CommunityDao;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.AccessType;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
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
    private final SimpleJdbcInsert accessJdbcInsert;

    private final static RowMapper<Community> ROW_MAPPER = (rs, rowNum) -> new Community(rs.getLong("community_id"),
            rs.getString("name"),
            rs.getString("description"),
            new User(rs.getLong("moderator_id"), rs.getString("user_name"), rs.getString("user_email"), rs.getString("password")));


    private final String MAPPED_QUERY = "SELECT community_id, community.name, description, moderator_id, username AS user_name, email AS user_email, users.password FROM community JOIN users on community.moderator_id = user_id ";

    @Autowired
    public CommunityJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("community")
                .usingGeneratedKeyColumns("community_id");

        accessJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("access")
                .usingGeneratedKeyColumns("access_id");

    }

    @Override
    public Community create(String name, String description, User moderator){
        final Map<String, Object> args = new HashMap<>();
        args.put("name", name);
        args.put("description", description);
        args.put("moderator_id", moderator.getId());
        final Number id = jdbcInsert.executeAndReturnKey(args);
        return new Community(id.longValue(), name, description, moderator);
    }

    @Override
    public  Optional<Community> findById(Number id ){ return jdbcTemplate.query(MAPPED_QUERY + " where community_id = ?" , ROW_MAPPER , id.longValue()).stream().findFirst();};

    @Override
    public List<Community> list(){
        return jdbcTemplate.query(MAPPED_QUERY , ROW_MAPPER);
    };

    @Override
    public List<Community> getByModerator(Number moderatorId, int offset, int limit) {
        return jdbcTemplate.query(MAPPED_QUERY + "WHERE moderator_id = ? order by community_id desc offset ? limit ? ", ROW_MAPPER, moderatorId.longValue(), offset, limit);
    }

    @Override
    public void updateAccess(Number userId, Number communityId, AccessType type) {

        //Si quieren reestablecer el acceso del usuario
        if(type == null){
            jdbcTemplate.update("DELETE FROM access WHERE user_id = ? and community_id = ?", userId.longValue(), communityId.longValue());
            return;
        }

        final Map<String, Object> args = new HashMap<>();
        args.put("user_id", userId.longValue());
        args.put("community_id", communityId.longValue());
        args.put("access_type", type.ordinal());
        try{
        accessJdbcInsert.executeAndReturnKey(args);
        }
        catch(DuplicateKeyException e){
            jdbcTemplate.update("UPDATE access set access_type = ? where community_id = ? and user_id = ?", type.ordinal(), communityId.longValue(), userId.longValue());
        }
    }

    @Override
    public Optional<AccessType> getAccess(Number userId, Number communityId) {
        RowMapper<AccessType> ACCESS_ROW_MAPPER = (rs, rowNum) -> AccessType.valueOf(rs.getInt("access_type"));
        List<AccessType> rs =  jdbcTemplate.query("SELECT access_type FROM access where user_id = ? and community_id = ?", ACCESS_ROW_MAPPER, userId.longValue(), communityId.longValue());
        return rs.stream().findFirst();
    }
}
