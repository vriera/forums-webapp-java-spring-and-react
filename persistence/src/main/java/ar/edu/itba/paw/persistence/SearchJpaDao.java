package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistance.SearchDao;
import ar.edu.itba.paw.models.*;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Primary
@Repository
public class SearchJpaDao implements SearchDao {

    @PersistenceContext
    private EntityManager em;

    private static final String SEARCH_QUERY = "search_query";
    private static final String LIKE_QUERY = "like_query";
    private static final String USER_ID = "user_id";

    @Override
    public List<Question> search(SearchFilter filter, SearchOrder order, Long communityId, long userId, int limit,
            int offset) {

        StringBuilder rawSelect = new StringBuilder("select * from ( ");
        rawSelect.append(SearchUtils.RAW_SELECT);
        rawSelect.append(
                " where ( (community.community_id = access.community_id and access.user_id = :user_id) or community.moderator_id = 0 or community.moderator_id = :user_id )");
        SearchUtils.appendFilter(rawSelect, filter.ordinal());
        if (communityId != null && communityId >= 0) {
            rawSelect.append(" and community.community_id = ");
            rawSelect.append(communityId);
            rawSelect.append(" ");
        }
        SearchUtils.appendOrder(rawSelect, order.ordinal(), false);
        Query nativeQuery = em.createNativeQuery(rawSelect.toString(), Question.class);
        nativeQuery.setParameter(USER_ID, userId);
        if (limit != -1 && offset != -1) {
            nativeQuery.setFirstResult(offset);
            nativeQuery.setMaxResults(limit);
        }
        @SuppressWarnings("unchecked")
        List<Question> questionList = nativeQuery.getResultList();
        return questionList;
    }

    @Override
    public long searchUserCount(String query) {

        if (query == null || query.length() == 0) {
            Query nativeQuery = em.createNativeQuery("select count(*) from users ");
            return ((Number) nativeQuery.getSingleResult()).longValue();
        }
        query = SearchUtils.prepareQuery(query);
        query = query.toLowerCase();
        Query nativeQuery = em.createNativeQuery("select count(*)" +
                " from users, plainto_tsquery('spanish' , :search_query) as query " +
                "where LOWER(username) like (:like_query) or to_tsvector('spanish' , LOWER(username)) @@ query");
        nativeQuery.setParameter(SEARCH_QUERY, query);
        nativeQuery.setParameter(LIKE_QUERY, "%" + query + "%");
        return ((Number) nativeQuery.getSingleResult()).longValue();
    }

    @Override
    public List<User> searchUser(String query, int limit, int offset) {

        if (query == null || query.length() == 0) {
            Query nativeQuery = em.createNativeQuery("select users.user_id from users ");
            if (limit != -1 && offset != -1) {
                nativeQuery.setFirstResult(offset);
                nativeQuery.setMaxResults(limit);
            }
            @SuppressWarnings("unchecked")
            List<Long> id = (List<Long>) nativeQuery.getResultList().stream().map(e -> Long.valueOf(e.toString()))
                    .collect(Collectors.toList());
            if (id.isEmpty())
                return Collections.emptyList();

            final TypedQuery<User> typedQuery = em.createQuery("select u from User u where id IN :idList", User.class);
            typedQuery.setParameter("idList", id.stream().map(Long::new).collect(Collectors.toList()));
            return typedQuery.getResultList().stream().collect(Collectors.toList());
        }
        query = SearchUtils.prepareQuery(query);
        query = query.toLowerCase();

        Query nativeQuery = em.createNativeQuery("select users.user_id , " +
                "username , email , " +
                "password from users, plainto_tsquery('spanish' , :search_query) as query " +
                "where LOWER(username) like (:like_query) or to_tsvector('spanish' , LOWER(username)" +
                ") " +
                "@@ query order by  " +
                "coalesce(ts_rank_cd(to_tsvector('spanish' ,LOWER(username)) , query, 32),0)", User.class);
        if (limit != -1 && offset != -1) {
            nativeQuery.setMaxResults(limit);
            nativeQuery.setFirstResult(offset);
        }
        nativeQuery.setParameter(SEARCH_QUERY, query);
        nativeQuery.setParameter(LIKE_QUERY, "%" + query + "%");

        return nativeQuery.getResultList();
    }

    @Override
    public long searchCommunityCount(String query) {
        query = SearchUtils.prepareQuery(query);
        query = query.toLowerCase();
        Query nativeQuery = em.createNativeQuery("select count(*)  " +
                " from community , plainto_tsquery('spanish' , :search_query) as query " +
                "where LOWER(name) like (:like_query) " +
                "or LOWER(description) like (:like_query) " +
                "or to_tsvector('spanish', LOWER(name)) @@ query or to_tsvector('spanish', LOWER(description)) @@ query ");
        nativeQuery.setParameter(SEARCH_QUERY, query);
        nativeQuery.setParameter(LIKE_QUERY, "%" + query + "%");
        return ((Number) nativeQuery.getSingleResult()).longValue();
    }

    @Override
    public List<Community> searchCommunity(String query, int limit, int offset) {
        query = SearchUtils.prepareQuery(query);
        query = query.toLowerCase();
        Query nativeQuery = em.createNativeQuery("select community_id ," +
                " name , description , moderator_id  " +
                "from community , plainto_tsquery('spanish' , :search_query) as query " +
                "where LOWER(name) like (:like_query) " +
                "or LOWER(description) like (:like_query) " +
                "or to_tsvector('spanish', LOWER(name)) @@ query or to_tsvector('spanish', LOWER(description)) @@ query "
                +
                "order by coalesce(ts_rank_cd(to_tsvector('spanish' ,LOWER(name)) , query , 32),0) + " +
                "coalesce(ts_rank_cd(to_tsvector('spanish' ,LOWER(description)) , query , 32),0) ", Community.class);
        if (limit != -1 && offset != -1) {
            nativeQuery.setMaxResults(limit);
            nativeQuery.setFirstResult(offset);
        }
        nativeQuery.setParameter(SEARCH_QUERY, query);
        nativeQuery.setParameter(LIKE_QUERY, "%" + query + "%");
        return (List<Community>) nativeQuery.getResultList();
    }

    @Override
    public List<Answer> getTopAnswers(Number userId) {
        TypedQuery<Answer> query = em.createQuery("select a from Karma k join Answer a on (k.user.id = a.owner.id) " +
                "join Question q on (q.id = a.question.id) join Forum f on (q.forum.id = f.id) " +
                "join Community c on (f.community.id = c.id) left join Access ac on ( c.id = ac.community.id and :userid = ac.user.id) "
                +
                "where k.karma > 0 and ( ac.accessType = :accesstype or c.moderator.id = :userid or c.moderator.id = 0) order by a.time desc",
                Answer.class);
        query.setParameter("accesstype", AccessType.ADMITTED);
        query.setParameter("userid", userId.longValue());
        query.setMaxResults(10);
        return query.getResultList();
    }

    @Override
    public long searchCount(SearchFilter filter, Long communityId, long userId) {

        StringBuilder rawSelect = new StringBuilder("select count(*) from ( ");
        rawSelect.append(SearchUtils.RAW_SELECT);
        rawSelect.append(
                " where ( (community.community_id = access.community_id and access.user_id = :user_id) or community.moderator_id = 0 or community.moderator_id = :user_id )");
        SearchUtils.appendFilter(rawSelect, filter.ordinal());
        if (communityId != null && communityId >= 0) {
            rawSelect.append(" and community.community_id = ");
            rawSelect.append(communityId);
            rawSelect.append(" ");
        }
        rawSelect.append(") as queryCount");
        Query nativeQuery = em.createNativeQuery(rawSelect.toString());
        nativeQuery.setParameter(USER_ID, userId);
        return ((Number) nativeQuery.getSingleResult()).longValue();
    }

    @Override
    public long searchCount(String query, SearchFilter filter, Long communityId, long userId) {
        query = SearchUtils.prepareQuery(query);
        StringBuilder mappedQuery = new StringBuilder("select count (*) from (");
        mappedQuery.append(SearchUtils.MAPPED_QUERY);
        mappedQuery.append(", plainto_tsquery('spanish', :search_query) query ");
        mappedQuery.append("WHERE (to_tsvector('spanish', title) @@ query ");
        mappedQuery.append("OR to_tsvector('spanish', body) @@ query ");
        mappedQuery.append(
                "OR ans_rank is not null OR title LIKE (:search_query_like) OR body LIKE (:search_query_like) ) ");
        mappedQuery.append(
                " and ( (community.community_id = access.community_id and access.user_id = :user_id) or community.moderator_id = 0 or community.moderator_id = :user_id )");
        if (communityId != null && communityId >= 0) {
            mappedQuery.append(" AND community.community_id = ");
            mappedQuery.append(communityId);
            mappedQuery.append(" ");
        }
        SearchUtils.appendFilter(mappedQuery, filter.ordinal());
        mappedQuery.append(") as queryCount");
        Query nativeQuery = em.createNativeQuery(mappedQuery.toString());
        nativeQuery.setParameter(SEARCH_QUERY, query);
        nativeQuery.setParameter("search_query_like", "%" + query + "%");
        nativeQuery.setParameter(USER_ID, userId);
        return ((Number) nativeQuery.getSingleResult()).longValue();
    }

    @Override
    public List<Question> search(String query, SearchFilter filter, SearchOrder order, Long communityId, long userId,
            int limit, int offset) {
        query = SearchUtils.prepareQuery(query);
        StringBuilder mappedQuery = new StringBuilder("select * from ( ");
        mappedQuery.append(SearchUtils.MAPPED_QUERY);
        mappedQuery.append(", plainto_tsquery('spanish', :search_query) query ");
        mappedQuery.append("WHERE (to_tsvector('spanish', title) @@ query ");
        mappedQuery.append("OR to_tsvector('spanish', body) @@ query ");
        mappedQuery.append(
                "OR ans_rank is not null OR title LIKE (:search_query_like) OR body LIKE (:search_query_like) ) ");
        mappedQuery.append(
                " and ( (community.community_id = access.community_id and access.user_id = :user_id) or community.moderator_id = 0 or community.moderator_id = :user_id )");
        if (communityId != null && communityId >= 0) {
            mappedQuery.append(" AND community.community_id = ");
            mappedQuery.append(communityId);
            mappedQuery.append(" ");
        }
        SearchUtils.appendFilter(mappedQuery, filter.ordinal());
        SearchUtils.appendOrder(mappedQuery, order.ordinal(), true);
        Query nativeQuery = em.createNativeQuery(mappedQuery.toString(), Question.class);
        nativeQuery.setParameter(SEARCH_QUERY, query);
        nativeQuery.setParameter("search_query_like", "%" + query + "%");
        nativeQuery.setParameter(USER_ID, userId);
        if (limit != -1 && offset != -1) {
            nativeQuery.setMaxResults(limit);
            nativeQuery.setFirstResult(offset);
        }

        @SuppressWarnings("unchecked")
        List<Question> questionList = nativeQuery.getResultList();
        return questionList;
    }

}
