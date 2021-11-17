package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistance.SearchDao;
import ar.edu.itba.paw.models.*;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
@Primary
@Repository
public class SearchJpaDao implements SearchDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Question> search(SearchFilter filter , SearchOrder order , Number community, User user, int limit, int offset) {

        StringBuilder rawSelect = new StringBuilder(SearchUtils.RAW_SELECT);
        rawSelect.append(" where ( (community.community_id = access.community_id and access.user_id = :user_id) or community.moderator_id = 0 or community.moderator_id = :user_id )");
        SearchUtils.appendFilter(rawSelect ,filter.ordinal());
        if( community.intValue() >= 0 ){
            rawSelect.append(" and community.community_id = ");
            rawSelect.append(community);
            rawSelect.append(" ");
        }
        SearchUtils.appendOrder(rawSelect , order.ordinal() , false);
        Query nativeQuery = em.createNativeQuery(rawSelect.toString() , Question.class);
        nativeQuery.setParameter("user_id" , user.getId());
        if( limit != -1 && offset != -1 ){
            nativeQuery.setFirstResult(offset);
            nativeQuery.setMaxResults(limit);
        }
        List<Question> questionList = ((List<Question>) nativeQuery.getResultList());
        /*if ( limit != 1 && offset != -1 ) {
            for ( Question question: questionList) {
                question.setLocalDate(question.getLocalDate());
            }
        }*/
        return questionList;
    }

    @Override
    public List<User> searchUser(String query , int limit , int offset) {
        query = SearchUtils.prepareQuery(query);
        query = query.toLowerCase();
       Query nativeQuery = em.createNativeQuery("select users.user_id , " +
               "username , email , " +
               "password from users, plainto_tsquery('spanish' , :search_query) as query " +
               "where LOWER(username) like (:like_query) or to_tsvector('spanish' , LOWER(username)" +
               ") " +
               "@@ query order by  " +
               "coalesce(ts_rank_cd(to_tsvector('spanish' ,LOWER(username)) , query, 32),0)" , User.class );
        if(limit!= -1 && offset!= -1) {
            nativeQuery.setMaxResults(limit);
            nativeQuery.setFirstResult(offset);
        }
        nativeQuery.setParameter("search_query" , query);
        nativeQuery.setParameter("like_query" , "%" + query + "%");
        return (List<User>) nativeQuery.getResultList();
    }

    @Override
    public List<Community> searchCommunity(String query, int limit , int offset) {
        query = SearchUtils.prepareQuery(query);
        query = query.toLowerCase();
        Query nativeQuery = em.createNativeQuery("select community_id ," +
                " name , description , moderator_id  "  +
                "from community , plainto_tsquery('spanish' , :search_query) as query " +
                "where LOWER(name) like (:like_query) " +
                "or LOWER(description) like (:like_query) " +
                "or to_tsvector('spanish', LOWER(name)) @@ query or to_tsvector('spanish', LOWER(description)) @@ query " +
                "order by coalesce(ts_rank_cd(to_tsvector('spanish' ,LOWER(name)) , query , 32),0) + " +
                "coalesce(ts_rank_cd(to_tsvector('spanish' ,LOWER(description)) , query , 32),0) " , Community.class);
        if(limit!= -1 && offset!= -1) {
            nativeQuery.setMaxResults(limit);
            nativeQuery.setFirstResult(offset);
        }
        nativeQuery.setParameter("search_query" , query);
        nativeQuery.setParameter("like_query" , "%" + query + "%");
        return (List<Community>) nativeQuery.getResultList();
    }
    @Override
    public List<Answer> getTopAnswers(Number userId){

        TypedQuery<Answer> query = em.createQuery("select a from Karma k join Answer a on (k.user.id = a.owner.id) " +
                "join Question q on (q.id = a.question.id) join Forum f on (q.forum.id = f.id) "+
                "join Community c on (f.community.id = c.id) left join Access ac on ( c.id = ac.community.id and :userid = ac.user.id) " +
                "where k.karma > 0 and ( ac.accessType = :accesstype or c.moderator.id = :userid or c.moderator.id = 0) order by a.time desc" , Answer.class);
        query.setParameter("accesstype" , AccessType.ADMITTED);
        query.setParameter("userid" , userId.longValue());
        query.setMaxResults(10);
        return query.getResultList();
    }


    @Override
    public List<Question> search(String query , SearchFilter filter , SearchOrder order  , Number community , User user , int limit , int offset) {
        query = SearchUtils.prepareQuery(query);
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
        SearchUtils.appendFilter(mappedQuery , filter.ordinal());
        SearchUtils.appendOrder(mappedQuery , order.ordinal() , true);
        Query nativeQuery = em.createNativeQuery(mappedQuery.toString() , Question.class);
        nativeQuery.setParameter("search_query" , query);
        nativeQuery.setParameter("search_query_like" , "%" + query + "%");
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
