package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistance.SearchDao;
import ar.edu.itba.paw.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Date;
import java.util.List;
@Repository
public class SearchJdbcDao implements SearchDao {

    private final JdbcTemplate jdbcTemplate;

    private final static RowMapper<User> USER_ROW_MAPPER = (rs, rowNum) -> new User(rs.getLong("user_id"), rs.getString("username"), rs.getString("email"), rs.getString("password"));


    private final static RowMapper<Community> COMMUNITY_ROW_MAPPER = (rs, rowNum) -> new Community(rs.getLong("community_id"),
            rs.getString("name"),
            rs.getString("description"),
            new User(rs.getLong("moderator_id"), rs.getString("username"), rs.getString("email"), ""));


    private final static RowMapper<Answer> ANSWER_ROW_MAPPER = (rs, rowNum) -> new Answer(
            rs.getLong("answer_id"),
            rs.getString("body"),
            rs.getBoolean("verify"),
            rs.getLong("question_id"),
            rs.getInt("votes"),
            new User(rs.getLong("user_id"), rs.getString("user_name"), rs.getString("user_email"), rs.getString("user_password"))
    );
    private final static RowMapper<Question> QUESTION_ROW_MAPPER = (rs, rowNum) -> new Question(
            rs.getLong("question_id"),
            new Date(rs.getTimestamp("time").getDate()),
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
            "                   else ts_rank_cd(to_tsvector('spanish' ,body) , ans_query , 32) end)  as ans_rank  " +
    "from answer left outer join answer_votes_summary on answer.answer_id = answer_votes_summary.answer_id , " +
            "plainto_tsquery('spanish',  ?) ans_query " +
            "WHERE to_tsvector('spanish', body) @@ ans_query ¿ " +
            "GROUP BY question_id "+
            "ORDER BY ans_rank) as aux_answers ";

    private final String MAPPED_QUERY =
            "SELECT coalesce(votes , 0 ) as votes , question.question_id, question.image_id , time, title, body, total_answers , users.user_id, users.username AS user_name, users.email AS user_email, users.password as user_password, " +
                    "community.community_id, community.name AS community_name, community.description, community.moderator_id, " +
                    " forum.forum_id, forum.name AS forum_name " +
                    "FROM question JOIN users ON question.user_id = users.user_id JOIN forum ON question.forum_id = forum.forum_id JOIN community ON forum.community_id = community.community_id LEFT OUTER JOIN access ON ( access.user_id = ? ) \n" +
                    "left join (Select question.question_id, sum(case when vote = true then 1 when vote = false then -1 end) as votes " +
                    "from question left join questionvotes as q on question.question_id = q.question_id group by question.question_id) as votes on votes.question_id = question.question_id left outer join " +
                    " (select question_id , sum(case when answer.verify = true then 1 else 0 end) as verified_match from answer group by question_id) as verified_search on question.question_id = verified_search.question_id left outer join "+
                    MAPPED_ANSWER_QUERY + " on  question.question_id = aux_answers.question_id left join (select question_id ,count(*) as total_answers from answer group by question_id) as aux2 on aux2.question_id = question.question_id ";

    private final String FULL_ANSWER = "Select coalesce(votes,0) as votes ,answer.answer_id, body, coalesce(verify,false) as verify, question_id, time ,  users.user_id, users.username AS user_name, users.email AS user_email, users.password AS user_password\n" +
            "    from answer JOIN users ON answer.user_id = users.user_id left join (Select answer.answer_id, sum(case when vote = true then 1 when vote = false then -1 end) as votes\n" +
            "from answer left join answervotes as a on answer.answer_id = a.answer_id group by answer.answer_id) votes on votes.answer_id = answer.answer_id";

    private final String RAW_SELECT =
            " SELECT coalesce(votes , 0 ) as votes , question.question_id, question.image_id , time, title, body , users.user_id, users.username AS user_name, users.email AS user_email, users.password as user_password,\n" +
            " community.community_id, community.name AS community_name, community.description, community.moderator_id,\n" +
            " forum.forum_id, forum.name AS forum_name\n" +
            " FROM question JOIN users ON question.user_id = users.user_id " +
                    "JOIN forum ON question.forum_id = forum.forum_id " +
                    "JOIN community ON forum.community_id = community.community_id LEFT OUTER JOIN access ON ( access.user_id = ? ) \n" +
            " left outer join (Select question.question_id, sum(case when vote = true then 1 when vote = false then -1 end) as votes\n" +
            "           from question left join " +
                    "questionvotes as q on question.question_id = q.question_id group by question.question_id) as votes " +
                    "on votes.question_id = question.question_id " +
                    "left outer join "+
                    "(select question_id ,\n" +
                    "        sum(total_votes) as total_answer_votes ,\n" +
                    "        sum(case when answer.verify = true then 1 else 0 end) as verified_match, "+
                    "        sum(vote_sum) as total_vote_sum from answer\n" +
                    "            left outer join answer_votes_summary on answer.answer_id = answer_votes_summary.answer_id group by question_id) " +
                    "as aux_answers on question.question_id = aux_answers.question_id left outer join" +
                    "(select question_id ,count(*) as total_answers from answer group by question_id) as aux2 on aux2.question_id = question.question_id ";

    @Autowired
    public SearchJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    private void appendFilter( StringBuilder mappedQuery , Number filter ){
        switch( filter.intValue()){
            case 1:
                mappedQuery.append(" and total_answers > 0 ");
                break;
            case 2:
                mappedQuery.append(" and ( total_answers = 0  or total_answers is null) ");
                break;
            case 3:
                mappedQuery.append(" and verified_match > 0 ");
                break;
        }
    }
    private void appendOrder(StringBuilder mappedQuery , Number order , Boolean hasText){

        mappedQuery.append(" ORDER BY ");
        switch ( order.intValue()){
            case 2:
                if( hasText) {
                    mappedQuery.append("ts_rank_cd(to_tsvector('spanish',title), query,32) + ts_rank_cd(to_tsvector('spanish',body), query,32) DESC ");
                }else{
                    mappedQuery.append(" time DESC ");
                }
                break;
            case 0:
                mappedQuery.append(" time DESC ");
                break;
            case 1:
                mappedQuery.append(" time ASC ");
                break;
            case 4:
                if ( hasText) {
                    mappedQuery.append(" coalesce(ans_rank,0) DESC ");
                }else{
                    mappedQuery.append(" coalesce(total_vote_sum,0) DESC ");
                }
                break;
            case 3:
                mappedQuery.append(" coalesce(votes,0) DESC ");
                break;
        }
    }
    @Override
    public List<Question> search(String query , Number filter , Number order , Number community , User user , int limit , int offset) {

        StringBuilder mappedQuery = new StringBuilder(MAPPED_QUERY.replace("¿"  , ""));
        mappedQuery.append(", plainto_tsquery('spanish', ?) query ");
        mappedQuery.append("WHERE (to_tsvector('spanish', title) @@ query ");
        mappedQuery.append("OR to_tsvector('spanish', body) @@ query ");
        mappedQuery.append("OR ans_rank is not null) ");
        mappedQuery.append(" and ( (community.community_id = access.community_id and access.user_id = ?) or community.moderator_id = 0 or community.moderator_id = ? )");
        if( community.intValue() >= 0 ){
            mappedQuery.append(" AND community.community_id = ");
            mappedQuery.append(community);
            mappedQuery.append(" ");
        }
        appendFilter(mappedQuery , filter);
        appendOrder(mappedQuery , order , true);
        if( limit != -1 && offset != -1 ){
            mappedQuery.append(" limit ");
            mappedQuery.append(limit);
            mappedQuery.append(" offset ");
            mappedQuery.append(offset);

        }
        System.out.println(mappedQuery);
        return jdbcTemplate.query( mappedQuery.toString() , QUESTION_ROW_MAPPER, user.getId() , query , query , user.getId() , user.getId());
    }

    @Override
    public List<Question> search(Number filter , Number order , Number community , User user, int limit , int offset){
        StringBuilder rawSelect = new StringBuilder(RAW_SELECT);
        rawSelect.append(" where ( (community.community_id = access.community_id and access.user_id = ?) or community.moderator_id = 0 or community.moderator_id = ? )");
        appendFilter(rawSelect ,filter);
        if( community.intValue() >= 0 ){
            rawSelect.append(" and community.community_id = ");
            rawSelect.append(community);
            rawSelect.append(" ");
        }
        appendOrder(rawSelect , order , false);
        if( limit != -1 && offset != -1 ){
            rawSelect.append(" limit ");
            rawSelect.append(limit);
            rawSelect.append(" offset ");
            rawSelect.append(offset);

        }
        System.out.println(rawSelect);
        return jdbcTemplate.query(rawSelect.toString() , QUESTION_ROW_MAPPER , user.getId() , user.getId() , user.getId());
    }
    @Override
    public List<User> searchUser(String query ){
        return jdbcTemplate.query("select * from users ,  plainto_tsquery('spanish',  ?) ans_query \n" +
                "WHERE to_tsvector('spanish', username) @@ ans_query or  to_tsvector('spanish', email) @@ ans_query \n" +
                "order by coalesce(ts_rank_cd(to_tsvector('spanish' ,username) , ans_query , 32),0) +coalesce(ts_rank_cd(to_tsvector('spanish' ,email) , ans_query , 32),0) desc " , USER_ROW_MAPPER , query);
    }
    @Override
    public List<Community> searchCommunity(String query){
        return jdbcTemplate.query("select * from community join users u on community.moderator_id = u.user_id,  plainto_tsquery('spanish',  ?) ans_query\n" +
                "WHERE to_tsvector('spanish', name) @@ ans_query or  to_tsvector('spanish', description) @@ ans_query\n" +
                "order by coalesce(ts_rank_cd(to_tsvector('spanish' ,name) , ans_query , 32),0) +coalesce(ts_rank_cd(to_tsvector('spanish' ,description) , ans_query , 32),0) desc" , COMMUNITY_ROW_MAPPER, query);
    }
    @Override
    public List<Answer> getTopAnswers(){
        return jdbcTemplate.query("select coalesce(votes,0) as votes , full_answers.answer_id, body, coalesce(verify , false) as verify , question_id, users.user_id, users.username AS user_name, users.email AS user_email, users.password AS user_password from\n" +
                "( " + FULL_ANSWER + " ) as full_answers " +
                "    join  (karma natural join users ) on users.user_id = full_answers.user_id\n" +
                "    where karma.karma > 0 and full_answers.time::date > now()::date-2\n" +
                "order by full_answers.time desc" , ANSWER_ROW_MAPPER );
    }

    public List<Question> searchByCommunity(String query, Number communityId) {
        String mappedQuery = MAPPED_QUERY.replace("¿"  , "");

        return jdbcTemplate.query(
                mappedQuery+
                        ", plainto_tsquery('spanish', ?) query " +
                        "WHERE (to_tsvector('spanish', title) @@ query " +
                        "OR to_tsvector('spanish', body) @@ query or ans_rank > 0) " +
                        "AND community.community_id = ?" +
                        "ORDER BY ts_rank_cd(to_tsvector('spanish',title), query) + " +
                        "ts_rank_cd(to_tsvector('spanish',body), query) DESC; ", QUESTION_ROW_MAPPER, query, communityId.longValue());
    }

}
