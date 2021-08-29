package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistance.QuestionDao;
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
public class QuestionJdbcDao implements QuestionDao {


    private JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final static RowMapper<Question> ROW_MAPPER = (rs, rowNum) -> new Question();

    @Autowired
    public QuestionJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("question")
                .usingGeneratedKeyColumns("id")
        .usingColumns("title" , "body" ,"user_id","community_id");
    }


    @Override
    public Optional<Question> findById(Long id ){
        return Optional.ofNullable(null);
    };

    @Override
    public List<Question> findAll(){
        return Collections.emptyList();
    };

    @Override
    public List<Question> findByCategory(Community community){
        return Collections.emptyList();
    };

    @Override
    public Question create(String title , String body , User owner , Community community) {
        final Map<String, Object> args = new HashMap<>();
        args.put("title", title); // la key es el nombre de la columna
        args.put("body", body);
        args.put("user_id" , owner.getUserid());
        args.put("community_id" , community.getId());
        final Number questionId = jdbcInsert.executeAndReturnKey(args);
        Optional<Question> q = findById(questionId.longValue());
        if( q.isPresent()){
            return q.get();
        }else{
            return null;
        }
    }

}
