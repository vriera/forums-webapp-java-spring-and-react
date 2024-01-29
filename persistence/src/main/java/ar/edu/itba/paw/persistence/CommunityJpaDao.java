package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistance.CommunityDao;
import ar.edu.itba.paw.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Repository
public class CommunityJpaDao implements CommunityDao {

	@PersistenceContext
	private EntityManager em;

	private static final Logger LOGGER = LoggerFactory.getLogger(CommunityJpaDao.class);

	@Override
	public List<Community> list(Number userId) {
		TypedQuery<Community> query = em.createQuery("select c from Community c left outer join Access a on (c.id = a.community.id and a.user.id = :userId) where c.moderator.id = :userId or c.moderator.id = 0 or a.accessType = :admittedType", Community.class);
		query.setParameter("userId", userId.longValue());
		query.setParameter("admittedType", AccessType.ADMITTED); //FIXME: se rompe cuando meto el join con Access
		return query.getResultList();
	}


	@Override
	public List<Community> getPublicCommunities() {
		TypedQuery<Community> query = em.createQuery("from Community c where c.moderator.id = 0", Community.class);
		query.setMaxResults(10);
		return query.getResultList();
	}

	private List<Long> publicList(Integer limit , Integer page){
		final String select = "SELECT c.community_id from Community c  where c.moderator_id = 0";
		Query nativeQuery = em.createNativeQuery(select);
		if(limit > 0 && page > 0) {
			nativeQuery.setFirstResult(limit*(page-1)); //offset
			nativeQuery.setMaxResults(limit);
		}
		@SuppressWarnings("unchecked")
		final List<Long> ids = (List<Long>) nativeQuery.getResultList().stream().map(e -> Long.valueOf(e.toString())).collect(Collectors.toList());
		return  ids;
	}

	private List<Long> privateList( Long userId , Integer limit , Integer page){
		final String select = "select c.community_id from  community as c left outer join access as a on ( c.community_id = a.community_id and a.user_id = :userId) where c.moderator_id =0 or c.moderator_id = :userId or a.access_type = :admittedType order by c.community_id asc";
		Query nativeQuery = em.createNativeQuery(select);
		nativeQuery.setParameter("userId", userId);
		nativeQuery.setParameter("admittedType", AccessType.ADMITTED); //FIXME: se rompe cuando meto el join con Access
		if(limit > 0 && page > 0) {
			nativeQuery.setFirstResult(limit*(page-1)); //offset
			nativeQuery.setMaxResults(limit);
		}
		@SuppressWarnings("unchecked")
		final List<Long> ids = (List<Long>) nativeQuery.getResultList().stream().map(e -> Long.valueOf(e.toString())).collect(Collectors.toList());
		return  ids;
	}

	public List<Community> list(Long userId , Integer limit , Integer page){
		List<Long> ids;
		if(userId == -1) ids = publicList(limit, page);
		else ids = privateList(userId ,  limit ,  page);
		TypedQuery<Community> query = em.createQuery("from Community where id IN :questionIds", Community.class);
		query.setParameter("questionIds" , ids);
		if(limit > 0 && page > 0) {
			query.setFirstResult(limit*(page-1)); //offset
			query.setMaxResults(limit);
		}
		return query.getResultList().stream().sorted((o1,o2)-> o1.getId().compareTo(o2.getId())).collect(Collectors.toList());

	}
	public long listCount(Number userId){
		return list(userId).size();
	}


	@Override
	public Optional<Community> findById(Number id) {
		return Optional.ofNullable(em.find(Community.class, id.longValue()));
	}

	@Override
	public Optional<Community> findByName(String name) {
		TypedQuery<Community> query = em.createQuery("from Community where name = :name", Community.class);
		query.setParameter("name" , name);
		return query.getResultList().stream().findFirst();
	}

	@Override
	public Community create(String name, String description, User moderator) {
		Community community = new Community(null, name, description, moderator);
		em.persist(community);
		LOGGER.debug("Comunidad creada: {}: {} con id {}. Moderador {} con id {}",
				community.getName(), community.getDescription(), community.getId(), community.getModerator().getUsername(), community.getModerator().getId());
		return community;
	}

	@Override
	public List<Community> getByModerator(Long moderatorId, Integer page, Integer limit) {
		Query query = em.createNativeQuery("select community_id from community where moderator_id = :moderatorId");
		query.setParameter("moderatorId" , moderatorId);
		if(limit!= null && page!= null && limit > 0 && page > 0) {
			query.setFirstResult(limit*(page-1)); //offset
			query.setMaxResults(limit);
		}
		List<Integer> idList = (List<Integer>)query.getResultList();
		if(idList.isEmpty())
			return Collections.emptyList();
		final TypedQuery<Community> typedQuery = em.createQuery("select c from Community c where id IN :idList", Community.class);
		typedQuery.setParameter("idList", idList.stream().map(Long::new).collect(Collectors.toList()));
		return typedQuery.getResultList().stream().collect(Collectors.toList());
	}

	@Override
	public long getByModeratorCount(Long moderatorId) {
		Query query = em.createQuery("select count(c.id) from Community c where c.moderator.id = :moderatorId");
		query.setParameter("moderatorId", moderatorId);
		return (Long) query.getSingleResult();
	}

	@Override
	public List<Community> getCommunitiesByAccessType(Long userId, AccessType type, Integer page, Integer limit) {
		String select = "SELECT access.community_id from access where access.user_id = :userId";
		if(type != null)
			select+= " and access.access_type = :type";
		Query nativeQuery = em.createNativeQuery(select);
		nativeQuery.setParameter("userId", userId);
		if(limit > 0 && page > 0) {
			nativeQuery.setFirstResult(limit*(page-1)); //offset
			nativeQuery.setMaxResults(limit);
		}

		if(type != null)
			nativeQuery.setParameter("type", type.ordinal());

		@SuppressWarnings("unchecked")
		final List<Integer> communityIds = (List<Integer>) nativeQuery.getResultList();

		if(communityIds.isEmpty()){
			return Collections.emptyList();
		}

		final TypedQuery<Community> query = em.createQuery("from Community where id IN :communityIds", Community.class);
		query.setParameter("communityIds", communityIds.stream().map(Long::new).collect(Collectors.toList()));

		return query.getResultList().stream().collect(Collectors.toList());


		/*
		String queryString = "select a.community from Access a where a.user.id = :userId";
		if(type != null)
			queryString+= " and a.accessType = :type";

		TypedQuery<Community> query = em.createQuery(queryString, Community.class);
		query.setParameter("userId", userId.longValue());
		query.setFirstResult(offset.intValue());
		query.setMaxResults(limit.intValue());

		if(type != null)
			query.setParameter("type", type);

		return query.getResultList();

		 */
	}

	@Override
	public long getCommunitiesByAccessTypeCount(Number userId, AccessType type) {
		String queryString = "select count(distinct a.community.id) from Access a where a.user.id = :userId";
		if(type != null){
			queryString+= " and a.accessType = :type";
		}

		Query query = em.createQuery(queryString);
		query.setParameter("userId", userId.longValue());

		if(type != null)
			query.setParameter("type", type);

		return (Long) query.getSingleResult();
	}

	@Override
	@Transactional
	public void updateAccess(Number userId, Number communityId, AccessType type) {

		//Si quieren reestablecer el acceso del usuario
		if(type == null){
			Query deleteQuery = em.createQuery("delete from Access a where a.community.id = :communityId and a.user.id = :userId");
			deleteQuery.setParameter("communityId", communityId.longValue());
			deleteQuery.setParameter("userId", userId.longValue());
			deleteQuery.executeUpdate();
			return;
		}

		TypedQuery<Access> query = em.createQuery("select a from Access a where a.community.id = :communityId and a.user.id = :userId", Access.class);
		query.setParameter("communityId", communityId.longValue());
		query.setParameter("userId", userId.longValue());

		Optional<Access> result = query.getResultList().stream().findFirst();

		Community c = em.find(Community.class, communityId.longValue());
		User u = em.find(User.class, userId.longValue());
		Access access = new Access(null, c, u, type);

		if(result.isPresent()) {
			access.setId(result.get().getId());
			em.merge(access);
		}
		else{
			em.persist(access);
		}

	}

	@Override
	public Optional<AccessType> getAccess(Long userId, Long communityId) {
		TypedQuery<AccessType> query = em.createQuery("select a.accessType from Access a where a.community.id = :communityId and a.user.id = :userId", AccessType.class);
		query.setParameter("communityId", communityId.longValue());
		query.setParameter("userId", userId.longValue());
		return query.getResultList().stream().findFirst();
	}

	@Override
	public List<CommunityNotifications> getCommunityNotifications(Number moderatorId) {
		TypedQuery<CommunityNotifications> query =  em.createQuery("select c from CommunityNotifications c where c.moderator.id = :moderatorId", CommunityNotifications.class);
		query.setParameter("moderatorId", moderatorId.longValue());
		return query.getResultList();
	}

	@Override
	public Optional<CommunityNotifications> getCommunityNotificationsById(Number communityId) {
		TypedQuery<CommunityNotifications> query =  em.createQuery("select c from CommunityNotifications c where c.community.id = :communityId", CommunityNotifications.class);
		query.setParameter("communityId", communityId.longValue());
		return query.getResultList().stream().findFirst();
	}

	@Override
	public Optional<Number> getUserCount(Number communityId){
		Query query = em.createNativeQuery("select count(*) as count from access where community_id = :communityId and access_type = 0" );
		query.setParameter("communityId" , communityId.longValue());
		List<Number> result = (List<Number>)query.getResultList();
		return result.stream().findFirst();
	}

}
