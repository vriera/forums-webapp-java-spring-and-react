package ar.edu.itba.paw.models;

import javax.persistence.*;

@Table(name = "images")
@Entity
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id", nullable = false)
    private Number id;

    @Column(name = "image", nullable = false)
    private byte[] image;

    public Image(Number id, byte[] image) {
        this.id = id;
        this.image = image;
    }

    public Image() {
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public Number getId() {
        return id;
    }

    public void setId(Number id) {
        this.id = id;
    }
}