package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistance.SearchDao;
import ar.edu.itba.paw.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
@Repository
public class SearchJdbcDao implements SearchDao {

    private final JdbcTemplate jdbcTemplate;

    private final static RowMapper<Question> ROW_MAPPER = (rs, rowNum) -> new Question(
            rs.getLong("question_id"),
            new SmartDate(rs.getTimestamp("time")),
            rs.getString("title"), rs.getString("body"),rs.getInt("votes"),
            new User(rs.getLong("user_id"), rs.getString("user_name"), rs.getString("user_email"), rs.getString("user_password")),
            new Community(rs.getLong("community_id"), rs.getString("community_name"), rs.getString("description"),
                    new User(rs.getLong("moderator_id"), rs.getString("user_name"), rs.getString("user_email"), rs.getString("user_password"))),
            new Forum(rs.getLong("forum_id"), rs.getString("forum_name"),
                    new Community(rs.getLong("community_id"), rs.getString("community_name"), rs.getString("description"),
                            new User(rs.getLong("moderator_id"), rs.getString("user_name"), rs.getString("user_email"), rs.getString("user_password"))))
            , rs.getInt("image_id"));

    private final String MAPPED_ANSWER_QUERY = "(select answer.answer_id  as answer_id," +
            "verify , question_id , total_votes as total_answer_votes, vote_sum as answer_vote_sum, "+
            "ts_rank_cd(to_tsvector('spanish' ,body) , ans_query , 32)  as ans_rank from answer left outer join votes_summary on answer.answer_id = votes_summary.answer_id , " +
            "plainto_tsquery('spanish',  ?) ans_query " +
            "WHERE to_tsvector('spanish', body) @@ ans_query "+
            "ORDER BY ts_rank_cd(to_tsvector('spanish',body), ans_query) DESC) as aux_answers ";

    private final String MAPPED_QUERY =
            "SELECT votes, question.question_id, question.image_id , time, title, body, users.user_id, users.username AS user_name, users.email AS user_email, users.password as user_password, " +
                    "community.community_id, community.name AS community_name, community.description, community.moderator_id, " +
                    " forum.forum_id, forum.name AS forum_name " +
                    "FROM question JOIN users ON question.user_id = users.user_id JOIN forum ON question.forum_id = forum.forum_id JOIN community ON forum.community_id = community.community_id " +
                    "left join (Select question.question_id, sum(case when vote = true then 1 when vote = false then -1 end) as votes " +
                    "from question left join questionvotes as q on question.question_id = q.question_id group by question.question_id) as votes on votes.question_id = question.question_id left outer join "+
                    MAPPED_ANSWER_QUERY + " on  question.question_id = aux_answers.question_id";





    @Autowired
    public SearchJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Override
    public List<Question> search(String query) {

        String s = MAPPED_QUERY + ", plainto_tsquery('spanish', ?) query " +
                "WHERE to_tsvector('spanish', title) @@ query " +
                "OR to_tsvector('spanish', body) @@ query " +
                "OR ans_rank > 0 " +
                "ORDER BY ts_rank_cd(to_tsvector('spanish',title), query) + " +
                "ts_rank_cd(to_tsvector('spanish',body), query) DESC; ";
        System.out.println((s.replace( "?" , query)));

        return jdbcTemplate.query(
                MAPPED_QUERY +
                        ", plainto_tsquery('spanish', ?) query " +
                        "WHERE to_tsvector('spanish', title) @@ query " +
                        "OR to_tsvector('spanish', body) @@ query " +
                        "OR ans_rank > 0 " +
                        "ORDER BY ts_rank_cd(to_tsvector('spanish',title), query) + " +
                        "ts_rank_cd(to_tsvector('spanish',body), query) DESC; ", ROW_MAPPER, query , query);
    }


    public List<Question> searchByCommunity(String query, Number communityId) {
        return jdbcTemplate.query(
                MAPPED_QUERY +
                        ", plainto_tsquery('spanish', ?) query " +
                        "WHERE (to_tsvector('spanish', title) @@ query " +
                        "OR to_tsvector('spanish', body) @@ query) " +
                        "AND community.community_id = ?" +
                        "ORDER BY ts_rank_cd(to_tsvector('spanish',title), query) + " +
                        "ts_rank_cd(to_tsvector('spanish',body), query) DESC; ", ROW_MAPPER, query, communityId.longValue());
    }

}
