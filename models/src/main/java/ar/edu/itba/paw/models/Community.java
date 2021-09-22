package ar.edu.itba.paw.models;

public class Community {


    private Long id;

    private String name;

    private String description;

    private User moderator;


    public Community(){};


//    public Community(long id, String name) {
//        this.id = id;
//        this.name = name;
//    }


    //este metodo no se fue por que lo usa valchar para el temporary question y porque no me anime
    //a cambiar el QuestionJDBCDaoTest
    //TODO: fletarlo limpio.
    public Community(long id, String name, String description){
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Community(long id, String name, String description, User moderator){
        this.id = id;
        this.name = name;
        this.description = description;
        this.moderator = moderator;
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

    public User getModerator() {
        return moderator;
    }

    public void setModerator(User moderator) {
        this.moderator = moderator;
    }
}
