package ar.edu.itba.paw.models;

public class User {
    private long userid;
    private String username;
    private String password;

    public User() {}

    public User(String username, String password, long userid) {
        this.username = username;
        this.password = password;
        this.userid = userid;
    }

    public long getUserid() {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
