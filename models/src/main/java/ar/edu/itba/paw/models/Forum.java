package ar.edu.itba.paw.models;

import javax.persistence.*;

@Entity
public class Forum {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "forum_forum_id_seq")
    @SequenceGenerator(name = "forum_forum_id_seq", sequenceName = "forum_forum_id_seq", allocationSize = 1)
    @Column(name = "forum_id")
    private Long id;

    @Column(name = "name", length = 250)
    private String name;

    @ManyToOne(optional = false)
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
