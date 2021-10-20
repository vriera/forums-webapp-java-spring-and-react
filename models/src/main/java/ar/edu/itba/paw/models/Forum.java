package ar.edu.itba.paw.models;


import javax.persistence.*;

@Entity
public class Forum {


    private String name;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="forum_forum_id_seq")
    @SequenceGenerator(name="forum_forum_id_seq", allocationSize=1)
    @Column(name= "forum_id")
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "community_id")
    private Community community;

    public Forum(){};

    public Forum(long id, String name, Community community) {
        this.name = name;
        this.id = id;
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

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }

}
