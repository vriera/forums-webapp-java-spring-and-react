package ar.edu.itba.paw.models;

public class Forum {


    private String name;
    private Long id;
    private Community community;

    public Forum(){};

    public Forum(Long id, String name, Community community) {
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
