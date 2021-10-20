package ar.edu.itba.paw.models;

import javax.persistence.*;

@Table(name = "community", indexes = {
        @Index(name = "community_name_key", columnList = "name", unique = true)
})
@Entity
public class Community {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "community_id", nullable = false)
    private Long id;

    @Column(name = "name", length = 250)
    private String name;

    @Lob
    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "moderator_id")
    private User moderator;

    public Community() {
    }

    public Community(Long id, String name, String description, User moderator) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.moderator = moderator;
    }

    public User getModerator() {
        return moderator;
    }

    public void setModerator(User moderator) {
        this.moderator = moderator;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}