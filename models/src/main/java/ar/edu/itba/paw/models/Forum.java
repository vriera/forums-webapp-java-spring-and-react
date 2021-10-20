package ar.edu.itba.paw.models;

import javax.persistence.*;

@Table(name = "forum")
@Entity
public class Forum {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "forum_id", nullable = false)
    private Long id;

    @Column(name = "name", length = 250)
    private String name;

    @ManyToOne
    @JoinColumn(name = "community_id")
    private Community community;

    public Forum(Long id, String name, Community community) {
        this.id = id;
        this.name = name;
        this.community = community;
    }

    public Forum() {
    }

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
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