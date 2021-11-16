package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistance.SearchDao;
import ar.edu.itba.paw.models.Answer;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Collections;
import java.util.List;
@Primary
@Repository
public class SearchJpaDao implements SearchDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Question> search(Number filter, Number order, Number community, User user, int limit, int offset) {
        StringBuilder rawSelect = new StringBuilder(SearchUtils.RAW_SELECT);
        rawSelect.append(" where ( (community.community_id = access.community_id and access.user_id = :user_id) or community.moderator_id = 0 or community.moderator_id = :user_id )");
        SearchUtils.appendFilter(rawSelect ,filter);
        if( community.intValue() >= 0 ){
            rawSelect.append(" and community.community_id = ");
            rawSelect.append(community);
            rawSelect.append(" ");
        }
        SearchUtils.appendOrder(rawSelect , order , false);
        Query nativeQuery = em.createNativeQuery(rawSelect.toString() , Question.class);
        nativeQuery.setParameter("user_id" , user.getId());
        if( limit != -1 && offset != -1 ){
            nativeQuery.setFirstResult(offset);
            nativeQuery.setMaxResults(limit);
        }
        List<Question> questionList = ((List<Question>) nativeQuery.getResultList());
        if ( limit != 1 && offset != -1 ) {
            for ( Question question: questionList) {
                question.setLocalDate(question.getLocalDate());
            }
        }
        return questionList;
    }

    @Override
    public List<User> searchUser(String query) {
        return Collections.emptyList();
    }

    @Override
    public List<Community> searchCommunity(String query) {
        return Collections.emptyList();
    }
    @Override
    public List<Answer> getTopAnswers(){
        return Collections.emptyList();
    }


    @Override
    public List<Question> search(String query , Number filter , Number order , Number community , User user , int limit , int offset) {
        StringBuilder mappedQuery = new StringBuilder(SearchUtils.MAPPED_QUERY);
        mappedQuery.append(", plainto_tsquery('spanish', :search_query) query ");
        mappedQuery.append("WHERE (to_tsvector('spanish', title) @@ query ");
        mappedQuery.append("OR to_tsvector('spanish', body) @@ query ");
        mappedQuery.append("OR ans_rank is not null OR title LIKE (:search_query_like) OR body LIKE (:search_query_like) ) ");
        mappedQuery.append(" and ( (community.community_id = access.community_id and access.user_id = :user_id) or community.moderator_id = 0 or community.moderator_id = :user_id )");
        if( community.intValue() >= 0 ){
            mappedQuery.append(" AND community.community_id = ");
            mappedQuery.append(community);
            mappedQuery.append(" ");
        }
        SearchUtils.appendFilter(mappedQuery , filter);
        SearchUtils.appendOrder(mappedQuery , order , true);
        Query nativeQuery = em.createNativeQuery(mappedQuery.toString().replace(":search_query_like" , "'%" + query + "%'") , Question.class);
        nativeQuery.setParameter("search_query" , query);

        nativeQuery.setParameter("user_id" , user.getId());
        if( limit != -1 && offset != -1){
            nativeQuery.setMaxResults(limit);
            nativeQuery.setFirstResult(offset);
        }

        List<Question> questionList = ((List<Question>) nativeQuery.getResultList());
        if ( limit != 1 && offset != -1 ) {
            for ( Question question: questionList) {
                question.setLocalDate(question.getLocalDate());
            }
        }
        return questionList;
    }


}
