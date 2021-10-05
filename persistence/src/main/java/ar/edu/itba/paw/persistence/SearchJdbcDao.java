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
    private final static RowMapper<Answer> ANSWER_ROW_MAPPER = (rs, rowNum) -> new Answer(
            rs.getLong("answer_id"),
            rs.getString("body"),
            rs.getBoolean("verify"),
            rs.getLong("question_id"),
            rs.getInt("votes"),
            new User(rs.getLong("user_id"), rs.getString("user_name"), rs.getString("user_email"), rs.getString("user_password"))
    );
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

    private final String MAPPED_ANSWER_QUERY = "(select question_id , "+
            "sum(case when total_votes is not null then ts_rank_cd(to_tsvector('spanish' ,body) , ans_query , 32) * (vote_sum)/(total_votes+1)\n" +
            "                   else ts_rank_cd(to_tsvector('spanish' ,body) , ans_query , 32) end)  as ans_rank , " +
    "sum( case when answer.verify = true then ( coalesce(ts_rank_cd(to_tsvector('spanish' ,body) , ans_query , 32),0)) else 0 end) as verified_match " +
    "from answer left outer join answer_votes_summary on answer.answer_id = answer_votes_summary.answer_id , " +
            "plainto_tsquery('spanish',  ?) ans_query " +
            "WHERE to_tsvector('spanish', body) @@ ans_query ¿ " +
            "GROUP BY question_id "+
            "ORDER BY ans_rank) as aux_answers ";

    private final String MAPPED_QUERY =
            "SELECT votes, question.question_id, question.image_id , time, title, body, total_answers , users.user_id, users.username AS user_name, users.email AS user_email, users.password as user_password, " +
                    "community.community_id, community.name AS community_name, community.description, community.moderator_id, " +
                    " forum.forum_id, forum.name AS forum_name " +
                    "FROM question JOIN users ON question.user_id = users.user_id JOIN forum ON question.forum_id = forum.forum_id JOIN community ON forum.community_id = community.community_id " +
                    "left join (Select question.question_id, sum(case when vote = true then 1 when vote = false then -1 end) as votes " +
                    "from question left join questionvotes as q on question.question_id = q.question_id group by question.question_id) as votes on votes.question_id = question.question_id left outer join "+
                    MAPPED_ANSWER_QUERY + " on  question.question_id = aux_answers.question_id left join (select question_id ,count(*) as total_answers from answer group by question_id) as aux2 on aux2.question_id = question.question_id ";

    private final String full_answers = "Select coalesce(votes,0) as votes ,answer.answer_id, body, coalesce(verify,false) as verify, question_id, time ,  users.user_id, users.username AS user_name, users.email AS user_email, users.password AS user_password\n" +
            "    from answer JOIN users ON answer.user_id = users.user_id left join (Select answer.answer_id, sum(case when vote = true then 1 when vote = false then -1 end) as votes\n" +
            "from answer left join answervotes as a on answer.answer_id = a.answer_id group by answer.answer_id) votes on votes.answer_id = answer.answer_id";
    @Autowired
    public SearchJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Override
    public List<Question> search(String query , Boolean hasAnswers , Boolean noAnswer , Boolean verifiedAnswer) {
        StringBuilder mapped_query = new StringBuilder(MAPPED_QUERY.replace("¿"  , ""));
        mapped_query.append(", plainto_tsquery('spanish', ?) query ");
        mapped_query.append("WHERE (to_tsvector('spanish', title) @@ query ");
        mapped_query.append("OR to_tsvector('spanish', body) @@ query ");
        mapped_query.append("OR ans_rank is not null) ");
        if( hasAnswers ){
            mapped_query.append(" and total_answers > 0 ");
        }else if(noAnswer){
            mapped_query.append(" and total_answers = 0 ");
        }
        if( verifiedAnswer){
            mapped_query.append(" and verified_match > 0 ");
        }
        System.out.println(mapped_query);
        return jdbcTemplate.query(
                mapped_query +
                        "ORDER BY ts_rank_cd(to_tsvector('spanish',title), query) + " +
                        "ts_rank_cd(to_tsvector('spanish',body), query) DESC; ", ROW_MAPPER, query , query);
    }

    public List<Answer> getTopAnswers(){
        return jdbcTemplate.query("select coalesce(votes,0) as votes , full_answers.answer_id, body, coalesce(verify , false) as verify , question_id, users.user_id, users.username AS user_name, users.email AS user_email, users.password AS user_password from\n" +
                "( " + full_answers + " ) as full_answers " +
                "    join  (karma natural join users ) on users.user_id = full_answers.user_id\n" +
                "    where karma.karma > 0 and full_answers.time::date > now()::date-2\n" +
                "order by full_answers.time desc" , ANSWER_ROW_MAPPER );
    }

    public List<Question> searchByCommunity(String query, Number communityId) {
        String mapped_query = MAPPED_QUERY.replace("¿"  , "");

        return jdbcTemplate.query(
                mapped_query+
                        ", plainto_tsquery('spanish', ?) query " +
                        "WHERE (to_tsvector('spanish', title) @@ query " +
                        "OR to_tsvector('spanish', body) @@ query or ans_rank > 0) " +
                        "AND community.community_id = ?" +
                        "ORDER BY ts_rank_cd(to_tsvector('spanish',title), query) + " +
                        "ts_rank_cd(to_tsvector('spanish',body), query) DESC; ", ROW_MAPPER, query, communityId.longValue());
    }

}
