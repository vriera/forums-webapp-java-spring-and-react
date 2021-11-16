package ar.edu.itba.paw.persistence;

public class SearchUtils {

    static public final String RAW_SELECT =
            " SELECT coalesce(votes , 0 ) as votes , question.question_id, question.image_id , time, title, body , users.user_id, users.username AS user_name, users.email AS user_email, users.password as user_password,\n" +
                    " community.community_id, community.name AS community_name, community.description, community.moderator_id,\n" +
                    " forum.forum_id, forum.name AS forum_name\n" +
                    " FROM question JOIN users ON question.user_id = users.user_id " +
                    "JOIN forum ON question.forum_id = forum.forum_id " +
                    "JOIN community ON forum.community_id = community.community_id LEFT OUTER JOIN access ON ( access.user_id = :user_id ) \n" +
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

    static private final String MAPPED_ANSWER_QUERY = "(select question_id , "+
            "coalesce(sum(case when total_votes is not null then ts_rank_cd(to_tsvector('spanish' ,body) , ans_query , 32) * (vote_sum)/(total_votes+1)\n" +
            "                   else ts_rank_cd(to_tsvector('spanish' ,body) , ans_query , 32) end) , 0)  as ans_rank  " +
            "from answer left outer join answer_votes_summary on answer.answer_id = answer_votes_summary.answer_id , " +
            "plainto_tsquery('spanish',  :search_query) ans_query " +
            "WHERE (to_tsvector('spanish', body) @@ ans_query OR body LIKE (:search_query_like) ) " +
            "GROUP BY question_id "+
            "ORDER BY ans_rank) as aux_answers ";

   public static final String MAPPED_QUERY =
            "SELECT coalesce(votes , 0 ) as votes , question.question_id, question.image_id , time, title, body, total_answers , users.user_id, users.username AS user_name, users.email AS user_email, users.password as user_password, " +
                    "community.community_id, community.name AS community_name, community.description, community.moderator_id, " +
                    " forum.forum_id, forum.name AS forum_name " +
                    "FROM question JOIN users ON question.user_id = users.user_id JOIN forum ON question.forum_id = forum.forum_id JOIN community ON forum.community_id = community.community_id LEFT OUTER JOIN access ON ( access.user_id = :user_id ) \n" +
                    "left join (Select question.question_id, sum(case when vote = true then 1 when vote = false then -1 end) as votes " +
                    "from question left join questionvotes as q on question.question_id = q.question_id group by question.question_id) as votes on votes.question_id = question.question_id left outer join " +
                    " (select question_id , sum(case when answer.verify = true then 1 else 0 end) as verified_match from answer group by question_id) as verified_search on question.question_id = verified_search.question_id left outer join "+
                    MAPPED_ANSWER_QUERY + " on  question.question_id = aux_answers.question_id left join (select question_id ,count(*) as total_answers from answer group by question_id) as aux2 on aux2.question_id = question.question_id ";


    public static final String FULL_ANSWER = "Select coalesce(votes,0) as votes ,answer.answer_id, body, coalesce(verify,false) as verify, question_id, time ,  users.user_id, users.username AS user_name, users.email AS user_email, users.password AS user_password\n" +
            "    from answer JOIN users ON answer.user_id = users.user_id left join (Select answer.answer_id, sum(case when vote = true then 1 when vote = false then -1 end) as votes\n" +
            "from answer left join answervotes as a on answer.answer_id = a.answer_id group by answer.answer_id) votes on votes.answer_id = answer.answer_id";

    public static void appendFilter( StringBuilder mappedQuery , Number filter ){
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
    public static String prepareQuery(String query){
        //saco espacios
        query = query.trim();
        //Escapo los caracteres que pueden generar problemas
        //https://stackoverflow.com/questions/32498432/add-escape-in-front-of-special-character-for-a-string
        final String[] metaCharacters = {"\\","^","$","{","}","[","]","(",")",".","*","+","?","|","<",">","-","&","%"};

        for (int i = 0 ; i < metaCharacters.length ; i++){
            if(query.contains(metaCharacters[i])){
                query = query.replace(metaCharacters[i],"\\"+metaCharacters[i]);
            }
        }
        return query;
    }
    public static void appendOrder(StringBuilder mappedQuery , Number order , Boolean hasText){

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

}
