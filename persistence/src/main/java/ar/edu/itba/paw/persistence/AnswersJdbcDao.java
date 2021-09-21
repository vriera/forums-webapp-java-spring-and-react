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
            new User(rs.getLong("user_id"), rs.getString("user_name"), rs.getString("user_email"))
            );




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
                "Select votes, answer.answer_id, body, verify, question_id, users.user_id, users.username AS user_name, users.email AS user_email\n" +
                        "from answer JOIN users ON answer.user_id = users.user_id left join (Select answer.answer_id, sum(case when vote = true then 1 when vote = false then -1 end) as votes\n" +
                        "from answer left join answervotes as a on answer.answer_id = a.answer_id group by answer.answer_id) votes on votes.answer_id = answer.answer_id where votes.answer_id = ?", ROW_MAPPER, id);

        return list.stream().findFirst();
    }

    @Override
    public List<Answer> findByQuestion(long question) {
        final List<Answer> list = jdbcTemplate.query(
                "Select votes, answer.answer_id, body, verify, question_id, users.user_id, users.username AS user_name, users.email AS user_email\n" +
                        "from answer JOIN users ON answer.user_id = users.user_id left join (Select answer.answer_id, sum(case when vote = true then 1 when vote = false then -1 end) as votes\n" +
                        "from answer left join answervotes as a on answer.answer_id = a.answer_id group by answer.answer_id) votes on votes.answer_id = answer.answer_id where question_id = ? order by verify, answer_id", ROW_MAPPER, question);

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
       jdbcTemplate.update("update Answer set verify = true where answer_id = ?", id);
       return findById(id);
    }

}
