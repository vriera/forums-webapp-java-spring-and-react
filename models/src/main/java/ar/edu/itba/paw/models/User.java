package ar.edu.itba.paw.models;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="users_user_id_seq")
    @SequenceGenerator(name="users_user_id_seq", sequenceName = "users_user_id_seq" , allocationSize=1)
    @Column(name= "user_id")
    private Long id;

    @Column(name="username" , length = 250)
    private String username;

    @Column(name = "email", length = 250)
    private String email;

    @Column(name = "password", length = 250)
    private String password;

    public User() {}

    public User(Long id, String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.id = id;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(username, user.username) && Objects.equals(email, user.email) && Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email, password);
    }
}
