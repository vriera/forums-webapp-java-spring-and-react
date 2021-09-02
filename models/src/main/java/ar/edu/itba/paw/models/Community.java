package ar.edu.itba.paw.models;

public class Community {


    private long id;

    private String name;


    public Community(){};


    public Community(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }


}
