package ar.edu.itba.paw.models;

import java.io.Serializable;

import javax.persistence.*;

@Table(name = "community", indexes = {
        @Index(name = "community_name_key", columnList = "name", unique = true)
})
@Entity
public class Community implements Serializable{


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "community_community_id_seq")
    @SequenceGenerator(name = "community_community_id_seq", sequenceName = "community_community_id_seq", allocationSize = 1)
    @Column(name = "community_id")
    private Long id;

    @Column(name = "name", length = 250)
    private String name;


    @Column(name = "description")
    private String description;

    @ManyToOne(optional = false)
    @JoinColumn(name = "moderator_id")
    private User moderator;


    @Transient
    public Long userCount;

    @Transient
    public Long notifications;

    public Long getNotifications() {
        return notifications;
    }

    public void setNotifications(Long notifications) {
        this.notifications = notifications;
    }

    public Community(){};

    public Community(Long id, String name, String description, User moderator) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.moderator = moderator;
    }

    public User getModerator() {
        return moderator;
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

    public void setModerator(User moderator) {
        this.moderator = moderator;
    }

    public Long getUserCount() { return userCount; }

    public void setUserCount(Long userCount) { this.userCount = userCount; }



}
