package ar.edu.itba.paw.models;

public class User {
    private Long id;
    private String username;


    private String email;

    public User() {}

    public User(long id, String username, String email) {
        this.username = username;
        this.email = email;
        this.id = id;
    }

    public User( String username, String email) {
        this.username = username;
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public void setId(long userid) {
        this.id = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email;  }

}
