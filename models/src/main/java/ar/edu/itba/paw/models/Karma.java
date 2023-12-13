package ar.edu.itba.paw.models;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import java.io.Serializable;

import javax.persistence.*;

@Entity
@Immutable
@Subselect("SELECT * FROM karma")
public class Karma implements Serializable {

    @Id
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "karma")
    private Long karma;

    public Karma(User user, Long karma) {
        this.user = user;
        this.karma = karma;
    }

    public Karma() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getKarma() {
        return karma;
    }

    public void setKarma(Long karma) {
        this.karma = karma;
    }

}
