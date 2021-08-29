package ar.edu.itba.paw.models;

public class User {
    private Long userid;
    private String username;


    private String email;

    public User() {}

    public User(String username, String email, long userid) {
        this.username = username;
        this.email = email;
        this.userid = userid;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
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
