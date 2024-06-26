package ar.edu.itba.paw.models;

import javax.persistence.*;

@Table(name = "access", indexes = {
        @Index(name = "access_community_id_user_id_key", columnList = "community_id, user_id", unique = true)
})
@Entity
public class Access {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "access_access_id_seq")
    @SequenceGenerator(name = "access_access_id_seq", sequenceName = "access_access_id_seq", allocationSize = 1)
    @Column(name = "access_id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    private Community community;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "access_type")
    @Enumerated(EnumType.ORDINAL)
    private AccessType accessType;

    public Access() {
    }

    public Access(Long id, Community community, User user, AccessType accessType) {
        this.id = id;
        this.community = community;
        this.user = user;
        this.accessType = accessType;
    }

    public AccessType getAccessType() {
        return accessType;
    }

    public void setAccessType(AccessType accessType) {
        this.accessType = accessType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}