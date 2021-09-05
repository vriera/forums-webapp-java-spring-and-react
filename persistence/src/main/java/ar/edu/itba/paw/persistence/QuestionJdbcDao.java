package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistance.QuestionDao;
import ar.edu.itba.paw.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.*;

@Repository
public class QuestionJdbcDao implements QuestionDao {


    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final static RowMapper<Question> ROW_MAPPER = (rs, rowNum) -> new Question(
            rs.getLong("question_id"),
            new SmartDate(rs.getTimestamp("time")),
            rs.getString("title"), rs.getString("body"),
            new User(rs.getLong("user_id"), rs.getString("user_name"), rs.getString("user_email")),
            new Community(rs.getLong("community_id"), rs.getString("community_name")),
            new Forum(rs.getLong("forum_id"), rs.getString("forum_name"),
                    new Community(rs.getLong("community_id"), rs.getString("community_name")))
            );

    @Autowired
    public QuestionJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("question")
                .usingGeneratedKeyColumns("question_id", "time");
    }

    @Override
    public Optional<Question> findById(long id ){
        final List<Question> list = jdbcTemplate.query(
                "SELECT question_id, time, title, body, " +
                "users.user_id, users.username AS user_name, users.email AS user_email, "+
                "community.community_id, community.name AS community_name, "+
                "forum.forum_id, forum.name AS forum_name "+
                "FROM question JOIN users ON question.user_id = users.user_id "+
                "JOIN forum ON question.forum_id = forum.forum_id "+
                "JOIN community ON forum.community_id = community.community_id "+
               "WHERE question_id = ?", ROW_MAPPER, id);
        return list.stream().findFirst();
    }

    @Override
    public List<Question> findAll(){
        return Collections.emptyList();
    }

    @Override
    public List<Question> findByForum(Number community_id, Number forum_id){
        final List<Question> list = jdbcTemplate.query(
                "SELECT question_id, time, title, body, " +
                "users.user_id, users.username AS user_name, users.email AS user_email, "+
                        "community.community_id, community.name AS community_name, "+
                        "forum.forum_id, forum.name AS forum_name "+
                        "FROM question JOIN users ON question.user_id = users.user_id "+
                        "JOIN forum ON question.forum_id = forum.forum_id "+
                        "JOIN community ON forum.community_id = community.community_id "+
                        "WHERE community.community_id = ? AND forum.forum_id = ?", ROW_MAPPER, community_id.longValue(), forum_id.longValue());

        return list;
    }

    @Override
    public Question create(String title , String body , User owner, Forum forum) {
        final Map<String, Object> args = new HashMap<>();
        args.put("title", title);
        args.put("body", body);
        args.put("user_id" , owner.getId());
        args.put("forum_id" , forum.getId());
        final Map<String, Object> keys = jdbcInsert.executeAndReturnKeyHolder(args).getKeys();
        Long id = ((Integer) keys.get("question_id")).longValue();
        SmartDate date = new SmartDate((Timestamp) keys.get("time"));

        return new Question(id, date, title, body, owner, forum.getCommunity(), forum);
    }

}
