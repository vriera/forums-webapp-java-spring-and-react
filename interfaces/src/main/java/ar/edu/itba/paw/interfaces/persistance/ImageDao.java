package ar.edu.itba.paw.interfaces.persistance;

import ar.edu.itba.paw.models.Image;

import java.util.Optional;

public interface ImageDao {
    Optional<Image> getImage(Number imageId);
    Image createImage(byte[] data);
}
