package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistance.ForumDao;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Forum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Primary
@Repository
public class ForumJpaDao implements ForumDao {
	@PersistenceContext
	private EntityManager em;

	private static final Logger LOGGER = LoggerFactory.getLogger(ForumJpaDao.class);

	@Override
	public List<Forum> list() {
		return em.createQuery("select f from Forum f", Forum.class).getResultList();
	}

	@Override
	public List<Forum> findByCommunity(Number communityId) {
		TypedQuery<Forum> query = em.createQuery("select f from Forum f where f.community.id = :communityId", Forum.class);
		query.setParameter("communityId", communityId.longValue());
		return query.getResultList();
	}

	@Override
	public Optional<Forum> findById(Number forumId) {
		return Optional.ofNullable(em.find(Forum.class, forumId.longValue()));
	}

	@Override
	@Transactional
	public Forum create(Community community) {
		Forum forum = new Forum(null, "General", community);
		em.persist(forum);
		LOGGER.debug("Foro creado: {} en {} con id {}", forum.getName(), forum.getCommunity().getName(), forum.getId());
		return forum;
	}
}
