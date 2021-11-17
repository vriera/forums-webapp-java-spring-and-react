package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistance.CommunityDao;
import ar.edu.itba.paw.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
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
	public Optional<Community> findById(Number id) {
		return Optional.ofNullable(em.find(Community.class, id.longValue()));
	}

	@Override
	public Optional<Community> findByName(String name) {
		return Optional.empty();
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
	public List<Community> getByModerator(Number moderatorId, Number offset, Number limit) {
		Query query = em.createNativeQuery("select community_id from community where moderator_id = :moderatorId");
		query.setParameter("moderatorId" , moderatorId.longValue());
		if(limit.intValue() != 1 && offset.intValue() != 1 ) {
			query.setFirstResult(offset.intValue());
			query.setMaxResults(limit.intValue());
		}
		List<Integer> idList = (List<Integer>)query.getResultList();
		if(idList.size() == 0 )
			return Collections.emptyList();
		final TypedQuery<Community> typedQuery = em.createQuery("select c from Community c where id IN :idList", Community.class);
		typedQuery.setParameter("idList", idList.stream().map(Long::new).collect(Collectors.toList()));
		List<Community> list = typedQuery.getResultList().stream().collect(Collectors.toList());
		return list;
	}

	@Override
	public long getByModeratorCount(Number moderatorId) {
		Query query = em.createQuery("select count(c.id) from Community c where c.moderator.id = :moderatorId");
		query.setParameter("moderatorId", moderatorId.longValue());
		return (Long) query.getSingleResult();
	}

	@Override
	public List<Community> getCommunitiesByAccessType(Number userId, AccessType type, Number offset, Number limit) {
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
	public Optional<AccessType> getAccess(Number userId, Number communityId) {
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
	};

}
