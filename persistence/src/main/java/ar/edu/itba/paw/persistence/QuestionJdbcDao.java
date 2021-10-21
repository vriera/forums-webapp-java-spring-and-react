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
    private final SimpleJdbcInsert jdbcInsertVotes;


    private final static RowMapper<Question> ROW_MAPPER = (rs, rowNum) -> new Question(
            rs.getLong("question_id"),
            new Date((rs.getTimestamp("time")).getDate()),
            rs.getString("title"), rs.getString("body"),rs.getInt("votes"),
            new User(rs.getLong("user_id"), rs.getString("user_name"), rs.getString("user_email"), rs.getString("user_password")),
            new Community(rs.getLong("community_id"), rs.getString("community_name"), rs.getString("description"),
                    new User(rs.getLong("moderator_id"), rs.getString("user_name"), rs.getString("user_email"), rs.getString("user_password"))),
            new Forum(rs.getLong("forum_id"), rs.getString("forum_name"),
                    new Community(rs.getLong("community_id"), rs.getString("community_name"), rs.getString("description"),
                            new User(rs.getLong("moderator_id"), rs.getString("user_name"), rs.getString("user_email"), rs.getString("user_password"))))
            , rs.getInt("image_id"));

    private final String MAPPED_QUERY =
            "SELECT votes, question.question_id, question.image_id , time, title, body, users.user_id, users.username AS user_name, users.email AS user_email, users.password as user_password, " +
                    "community.community_id, community.name AS community_name, community.description, community.moderator_id, " +
                    " forum.forum_id, forum.name AS forum_name " +
                    "FROM question JOIN users ON question.user_id = users.user_id JOIN forum ON question.forum_id = forum.forum_id JOIN community ON forum.community_id = community.community_id " +
                    "left join (Select question.question_id, sum(case when vote = true then 1 when vote = false then -1 else 0 end) as votes " +
                    "from question left join questionvotes as q on question.question_id = q.question_id group by question.question_id) as votes on votes.question_id = question.question_id ";


    private final static RowMapper<Long> COUNT_ROW_MAPPER = (rs, rowNum) -> rs.getLong("count");




    private final String MAPPED_ANSWER_QUERY = "select answer.answer_id  as answer_id," +
            "verify , question_id , total_votes as total_answer_votes, vote_sum as answer_vote_sum, "+
            "query  from answer left outer join votes_summary on answer.answer_id = votes_summary.answer_id , " +
            "plainto_tsquery('spanish',  ?) query " +
            "WHERE to_tsvector('spanish', body) @@ query "+
            "ORDER BY ts_rank_cd(to_tsvector('spanish',body), query) DESC";

    @Autowired
    public QuestionJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("question")
                .usingGeneratedKeyColumns("question_id", "time", "last_activity");
        jdbcInsertVotes = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("questionvotes")
                .usingGeneratedKeyColumns("votes_id");
    }

    @Override
    public Optional<Question> findById(long id ){
        //TODO: todo esto se podria reemplazar con el MAPPED_QUERY creo
        final List<Question> list = jdbcTemplate.query(
                MAPPED_QUERY +
                        "WHERE question.question_id = ?", ROW_MAPPER, id);
        return list.stream().findFirst();
    }

    @Override
    public List<Question> findAll(int limit, int offset){
        return jdbcTemplate.query(MAPPED_QUERY + "limit ? offset ?", ROW_MAPPER, limit, offset);
    }

    @Override
    public List<Question> findByForum(Number community_id, Number forum_id,  int limit, int offset){
        //TODO: parte 2 todo esto se podria reemplazar con el MAPPED_QUERY creo
        final List<Question> list = jdbcTemplate.query(
                MAPPED_QUERY +
                        "WHERE community.community_id = ? AND forum.forum_id = ? limit ? offset ?", ROW_MAPPER, community_id.longValue(), forum_id.longValue(), limit, offset);

        return list;
    }

    @Override
    public Question create(String title , String body , User owner, Forum forum , Number imageId) {
        final Map<String, Object> args = new HashMap<>();
        args.put("title", title);
        args.put("body", body);
        args.put("user_id" , owner.getId());
        args.put("forum_id" , forum.getId());
        args.put("image_id" , imageId);
        final Map<String, Object> keys = jdbcInsert.executeAndReturnKeyHolder(args).getKeys();
        long id = ((Integer) keys.get("question_id")).longValue();
        Date date = new Date(((Timestamp) keys.get("time")).getDate());

        return new Question(id, date, title, body, owner, forum.getCommunity(), forum , imageId);
    }


    @Override
    public void addVote(Boolean vote, Long user, Long questionId) {
        Optional<Long> voteId = jdbcTemplate.query("select votes_id from questionvotes where question_id=? AND user_id= ?", (rs, row) -> rs.getLong("votes_id"),questionId,user).stream().findFirst();
        if(!voteId.isPresent()) {
            final Map<String, Object> args = new HashMap<>();
            args.put("user_id", user);
            args.put("vote", vote);
            args.put("question_id", questionId);
            jdbcInsertVotes.executeAndReturnKeyHolder(args).getKeys();
            return;
        }
        jdbcTemplate.update("update questionvotes set vote = ?, user_id = ?, question_id = ? where votes_id=?",vote, user, questionId, voteId.get() );

    }

    @Override
    public List<Question> findByUser(long userId, int offset, int limit) {
        return jdbcTemplate.query(MAPPED_QUERY + "WHERE users.user_id = ? order by question_id desc offset ? limit ? ", ROW_MAPPER, userId, offset, limit);
    }

    @Override
    public int findByUserCount(long userId) {
        return jdbcTemplate.query("SELECT COUNT(*) as count FROM question WHERE user_id = ?", COUNT_ROW_MAPPER, userId).stream().findFirst().get().intValue();
    }



}