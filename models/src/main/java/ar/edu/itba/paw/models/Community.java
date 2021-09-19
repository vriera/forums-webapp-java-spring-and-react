package ar.edu.itba.paw.models;

public class Community {


    private Long id;

    private String name;

    private String description;


    public Community(){};


//    public Community(long id, String name) {
//        this.id = id;
//        this.name = name;
//    }

    public Community(long id, String name, String description){
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public void setId(Long id) {
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

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
