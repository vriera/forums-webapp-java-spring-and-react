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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class CommunityJpaDao implements CommunityDao {

	@PersistenceContext
	private EntityManager em;

	private static final Logger LOGGER = LoggerFactory.getLogger(CommunityJpaDao.class);

	private static final String USER_ID = "userId";
	private static final String ADMITTED_TYPE = "admittedType";
	private static final String MODERATOR_ID = "moderatorId";
	private static final String COMMUNITY_ID = "communityId";

	private List<Community> list(Number userId) {
		TypedQuery<Community> query = em.createQuery(
				"select c from Community c left outer join Access a on (c.id = a.community.id and a.user.id = :userId) where c.moderator.id = :userId or c.moderator.id = 0 or a.accessType = :admittedType",
				Community.class);
		query.setParameter(USER_ID, userId.longValue());
		query.setParameter(ADMITTED_TYPE, AccessType.ADMITTED);
		return query.getResultList();
	}

	private List<Long> publicList(Number limit, Number offset) {
		final String select = "SELECT c.community_id from Community c  where c.moderator_id = 0";
		Query nativeQuery = em.createNativeQuery(select);
		nativeQuery.setFirstResult(offset.intValue());
		nativeQuery.setMaxResults(limit.intValue());
		@SuppressWarnings("unchecked")
		final List<Long> ids = (List<Long>) nativeQuery.getResultList().stream().map(e -> Long.valueOf(e.toString()))
				.collect(Collectors.toList());
		return ids;
	}

	private List<Long> privateList(Number userId, Number limit, Number offset) {
		final String select = "select c.community_id from  community as c left outer join access as a on ( c.community_id = a.community_id and a.user_id = :userId) where c.moderator_id =0 or c.moderator_id = :userId or a.access_type = :admittedType order by c.community_id asc";
		Query nativeQuery = em.createNativeQuery(select);
		nativeQuery.setParameter(USER_ID, userId.longValue());
		nativeQuery.setParameter(ADMITTED_TYPE, AccessType.ADMITTED.ordinal());
		nativeQuery.setFirstResult(offset.intValue());
		nativeQuery.setMaxResults(limit.intValue());

		@SuppressWarnings("unchecked")
		final List<Long> ids = (List<Long>) nativeQuery.getResultList().stream().map(e -> Long.valueOf(e.toString()))
				.collect(Collectors.toList());
		return ids;
	}

	public List<Community> list(long userId, int limit, int offset) {
		List<Long> ids;
		if (userId == -1) {
			ids = publicList(limit, offset);
		} else {
			ids = privateList(userId, limit, offset);
		}
		TypedQuery<Community> query = em.createQuery("from Community where id IN :questionIds", Community.class);
		query.setParameter("questionIds", ids);
		query.setMaxResults(limit);

		return query.getResultList().stream().sorted((o1, o2) -> o1.getId().compareTo(o2.getId()))
				.collect(Collectors.toList());
	}

	public long listCount(long userId) {
		return list(userId).size();
	}

	@Override
	public Optional<Community> findById(long communityId) {
		return Optional.ofNullable(em.find(Community.class, communityId));
	}

	@Override
	public Optional<Community> findByName(String name) {
		TypedQuery<Community> query = em.createQuery("from Community where name = :name", Community.class);
		query.setParameter("name", name);
		return query.getResultList().stream().findFirst();
	}

	@Override
	public Community create(String name, String description, User moderator) {
		Community community = new Community(null, name, description, moderator);
		em.persist(community);
		LOGGER.debug("Comunidad creada: {}: {} con id {}. Moderador {} con id {}",
				community.getName(), community.getDescription(), community.getId(), community.getModerator().getUsername(),
				community.getModerator().getId());
		return community;
	}

	@Override
	public List<Community> getByModerator(long moderatorId, int offset, int limit) {
		Query query = em.createNativeQuery("select community_id from community where moderator_id = :moderatorId");
		query.setParameter(MODERATOR_ID, moderatorId);
		if (limit != 1 && offset != 1) {
			query.setFirstResult(offset);
			query.setMaxResults(limit);
		}

		@SuppressWarnings("unchecked")
		List<Integer> idList = query.getResultList();

		if (idList.isEmpty())
			return Collections.emptyList();

		final TypedQuery<Community> typedQuery = em.createQuery("select c from Community c where id IN :idList",
				Community.class);
		typedQuery.setParameter("idList", idList.stream().map(Long::new).collect(Collectors.toList()));
		return typedQuery.getResultList().stream().collect(Collectors.toList());
	}

	@Override
	public long getByModeratorCount(long moderatorId) {
		Query query = em.createQuery("select count(c.id) from Community c where c.moderator.id = :moderatorId");
		query.setParameter(MODERATOR_ID, moderatorId);
		return (Long) query.getSingleResult();
	}

	@Override
	public List<Community> getCommunitiesByAccessType(long userId, AccessType type, int offset, int limit) {
		String select = "SELECT access.community_id from access where access.user_id = :userId and access.access_type = :type";

		Query nativeQuery = em.createNativeQuery(select);
		nativeQuery.setParameter(USER_ID, userId);
		nativeQuery.setParameter("type", type.ordinal());
		nativeQuery.setFirstResult(offset);
		nativeQuery.setMaxResults(limit);

		@SuppressWarnings("unchecked")
		final List<Integer> communityIds = nativeQuery.getResultList();

		if (communityIds.isEmpty()) {
			return Collections.emptyList();
		}

		final TypedQuery<Community> query = em.createQuery("from Community where id IN :communityIds", Community.class);
		query.setParameter("communityIds", communityIds.stream().map(Long::new).collect(Collectors.toList()));

		return new ArrayList<>(query.getResultList());
	}

	@Override
	public long getCommunitiesByAccessTypeCount(long userId, AccessType type) {
		String queryString = "select count(distinct a.community.id) from Access a where a.user.id = :userId and a.accessType = :type";

		Query query = em.createQuery(queryString);
		query.setParameter(USER_ID, userId);
		query.setParameter("type", type);

		return (Long) query.getSingleResult();
	}

	@Override
	@Transactional
	public void updateAccess(long userId, long communityId, AccessType type) {

		if (type == AccessType.NONE) {
			Query deleteQuery = em
					.createQuery("delete from Access a where a.community.id = :communityId and a.user.id = :userId");
			deleteQuery.setParameter(COMMUNITY_ID, communityId);
			deleteQuery.setParameter(USER_ID, userId);
			deleteQuery.executeUpdate();
			return;
		}

		TypedQuery<Access> query = em.createQuery(
				"select a from Access a where a.community.id = :communityId and a.user.id = :userId", Access.class);
		query.setParameter(COMMUNITY_ID, communityId);
		query.setParameter(USER_ID, userId);

		Optional<Access> result = query.getResultList().stream().findFirst();

		Community c = em.find(Community.class, communityId);
		User u = em.find(User.class, userId);
		Access access = new Access(null, c, u, type);

		if (result.isPresent()) {
			access.setId(result.get().getId());
			em.merge(access);
		} else {
			em.persist(access);
		}

	}

	@Override
	public Optional<AccessType> getAccess(long userId, long communityId) {
		TypedQuery<AccessType> query = em.createQuery(
				"select a.accessType from Access a where a.community.id = :communityId and a.user.id = :userId",
				AccessType.class);
		query.setParameter(COMMUNITY_ID, communityId);
		query.setParameter(USER_ID, userId);
		return query.getResultList().stream().findFirst();
	}

	@Override
	public Optional<CommunityNotifications> getCommunityNotificationsById(long communityId) {
		TypedQuery<CommunityNotifications> query = em.createQuery(
				"select c from CommunityNotifications c where c.community.id = :communityId", CommunityNotifications.class);
		query.setParameter(COMMUNITY_ID, communityId);
		return query.getResultList().stream().findFirst();
	}

	@Override
	public Optional<Long> getUserCount(long communityId) {
		Query query = em.createNativeQuery(
				"select count(*) as count from access where community_id = :communityId and access_type = 0");
		query.setParameter(COMMUNITY_ID, communityId);

		@SuppressWarnings("unchecked")
		List<Number> result = query.getResultList();

		return result.stream().findFirst().map(Number::longValue);
	}

}
