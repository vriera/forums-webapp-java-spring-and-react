package ar.edu.itba.paw.models;

import javax.persistence.*;

@Table(name = "users", indexes = {
        @Index(name = "users_email_key", columnList = "email", unique = true)
})
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Integer id;

    @Column(name = "username", length = 250)
    private String username;

    @Column(name = "email", length = 250)
    private String email;

    @Column(name = "password", length = 250)
    private String password;

    //Para Hibernate
    public User(){}

    public User(Integer id, String username, String email, String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}