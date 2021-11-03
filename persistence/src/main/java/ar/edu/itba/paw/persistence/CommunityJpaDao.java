package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistance.CommunityDao;
import ar.edu.itba.paw.models.AccessType;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Primary
@Repository
public class CommunityJpaDao implements CommunityDao {

	@PersistenceContext
	private EntityManager em;

	private static final Logger LOGGER = LoggerFactory.getLogger(CommunityJpaDao.class);


	@Override
	public List<Community> list() {
		return em.createQuery("from Community as c", Community.class).getResultList();
	}

	@Override
	public Optional<Community> findById(Number id) {
		return Optional.ofNullable(em.find(Community.class, id.longValue()));
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
		TypedQuery<Community> query = em.createQuery("select c from Community c where c.moderator.id = :moderatorId", Community.class);
		query.setParameter("moderatorId", moderatorId.longValue());
		query.setFirstResult(offset.intValue());
		query.setMaxResults(limit.intValue());
		return query.getResultList();
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

		return query.getResultList();
	}

	@Override
	public long getCommunitiesByAccessTypeCount(Number userId, AccessType type) {
		String queryString = "select count(distinct a.community.id) from Access a where a.user.id = :userId";
		if(type != null)
			queryString+= " and a.accessType = :type";

		Query query = em.createQuery(queryString);
		query.setParameter("userId", userId.longValue());

		return (Long) query.getSingleResult();
	}

	@Override
	public void updateAccess(Number userId, Number communityId, AccessType type) {
		Query query = em.createQuery("update Access a set a.accessType = :accessType where a.user.id = :userId and a.community.id = :communityId");
		query.setParameter("accessType", type);
		query.setParameter("userId", userId.longValue());
		query.setParameter("communityId", communityId.longValue());
		query.executeUpdate();
	}

	@Override
	public Optional<AccessType> getAccess(Number userId, Number communityId) {
		TypedQuery<AccessType> query = em.createQuery("select a.accessType from Access a where a.community.id = :communityId and a.user.id = :userId", AccessType.class);
		query.setParameter("communityId", communityId.longValue());
		query.setParameter("userId", userId.longValue());
		return query.getResultList().stream().findFirst();
	}
}
