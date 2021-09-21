package ar.edu.itba.paw.persistence;


import ar.edu.itba.paw.interfaces.persistance.AnswersDao;
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
public class AnswersJdbcDao implements AnswersDao {

    private final JdbcTemplate jdbcTemplate; // comunicación base de datos.
    private final SimpleJdbcInsert jdbcInsert;


    private final static RowMapper<Answer> ROW_MAPPER = (rs, rowNum) -> new Answer(
            rs.getLong("answer_id"),
            rs.getString("body"),
            rs.getBoolean("verify"),
            rs.getLong("question_id"),
            new User(rs.getLong("user_id"), rs.getString("user_name"), rs.getString("user_email"), rs.getString("user_password"))
            );

    private final String MAPPED_QUERY =
            "SELECT answer_id, body, verify, question_id, users.user_id, users.username AS user_name, users.email AS user_email, users.password as user_password" +
            " FROM answer JOIN users ON answer.user_id = users.user_id ";


   @Autowired
    public AnswersJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("answer")
                .usingGeneratedKeyColumns("answer_id");
    }



    @Override
    public Optional<Answer> findById(long id ){
        final List<Answer> list = jdbcTemplate.query(
                MAPPED_QUERY + "WHERE answer_id = ?", ROW_MAPPER, id);

        return list.stream().findFirst();
    }

    @Override
    public List<Answer> findByQuestion(long question) {
        final List<Answer> list = jdbcTemplate.query(
                MAPPED_QUERY +"WHERE question_id = ? order by verify, answer_id", ROW_MAPPER, question);

        return list;
    }

    @Override
    public Answer create( String body ,User owner, Long question) {
        final Map<String, Object> args = new HashMap<>();
        args.put("body", body);
        args.put("user_id" , owner.getId());
        args.put("question_id" , question);
        args.put("verify",null);
        final Map<String, Object> keys = jdbcInsert.executeAndReturnKeyHolder(args).getKeys();
        Long id = ((Integer) keys.get("answer_id")).longValue();

        return new Answer(id,body,null,  question,owner);
    }

    @Override
    public Optional<Answer> verify(Long id){
       jdbcTemplate.update("update answer set verify = true where answer_id = ?", id);
       return findById(id);
    }

}
