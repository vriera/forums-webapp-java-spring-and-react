package ar.edu.itba.paw.models;

public class Image {
    private Number imageId;

    private byte[] image;

    public Image(){};
    public Image(Number imageId , byte[] image ){
        this.imageId = imageId;
        this.image = image;
    }
    public Number getImageId() {
        return imageId;
    }

    public void setImageId(Number imageId) {
        this.imageId = imageId;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
