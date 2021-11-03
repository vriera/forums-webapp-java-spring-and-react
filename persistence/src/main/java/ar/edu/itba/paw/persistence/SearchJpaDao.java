package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistance.SearchDao;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class SearchJpaDao implements SearchDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Question> search(String query , Number filter , Number order , Number community , User user , int limit , int offset) {

    }

    @Override
    public List<Question> search(String query , Number filter , Number order , Number community , User user , int limit , int offset) {





        StringBuilder mappedQuery = new StringBuilder(SearchUtils.MAPPED_QUERY);
        mappedQuery.append(", plainto_tsquery('spanish', ?) query ");
        mappedQuery.append("WHERE (to_tsvector('spanish', title) @@ query ");
        mappedQuery.append("OR to_tsvector('spanish', body) @@ query ");
        mappedQuery.append("OR ans_rank is not null OR title LIKE ('%:search_query%') OR body LIKE ('%:search_query%') ) ");
        mappedQuery.append(" and ( (community.community_id = access.community_id and access.user_id = ?) or community.moderator_id = 0 or community.moderator_id = ? )");
        if( community.intValue() >= 0 ){
            mappedQuery.append(" AND community.community_id = ");
            mappedQuery.append(community);
            mappedQuery.append(" ");
        }
        SearchUtils.appendFilter(mappedQuery , filter);
        SearchUtils.appendOrder(mappedQuery , order , true);
        if( limit != -1 && offset != -1 ){
            mappedQuery.append(" limit ");
            mappedQuery.append(limit);
            mappedQuery.append(" offset ");
            mappedQuery.append(offset);

        }
        System.out.println(mappedQuery);
        return jdbcTemplate.query( mappedQuery.toString().replace("" , query ) , QUESTION_ROW_MAPPER, user.getId() , query , query , user.getId() , user.getId());
    }

}
