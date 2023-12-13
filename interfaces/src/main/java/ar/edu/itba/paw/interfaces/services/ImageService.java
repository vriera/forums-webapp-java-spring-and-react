package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Image;

public interface ImageService {
    Image getImage(long imageId);

    Image createImage(byte[] data);
}
