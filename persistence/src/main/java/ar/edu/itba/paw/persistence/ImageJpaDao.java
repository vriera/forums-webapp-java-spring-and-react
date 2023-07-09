package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistance.ImageDao;
import ar.edu.itba.paw.models.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Primary
@Repository
public class ImageJpaDao implements ImageDao {

	@PersistenceContext
	private EntityManager em;

	private static final Logger LOGGER = LoggerFactory.getLogger(ImageJpaDao.class);


	@Override
	public Optional<Image> getImage(Number imageId) {
		return Optional.ofNullable(em.find(Image.class, imageId.longValue()));
	}

	@Override
	public Image createImage(byte[] data) {
		Image image = new Image(null,data);
		em.persist(image);
		LOGGER.debug("Imágen creada con id: {}", image.getId());
		return image;
	}
}
