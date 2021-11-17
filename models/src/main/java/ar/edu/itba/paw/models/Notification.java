package ar.edu.itba.paw.models;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Immutable
@Subselect("SELECT * FROM notifications")
public class Notification implements Serializable {

    @Id
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name="requests")
    private Long requests;

    @Column(name="invites")
    private Long invites;

    @Column(name="total")
    private Long total;

    public Notification(){};

    public Notification(User user, Long total, Long requests, Long invites) {
        this.user = user;
        this.requests = requests;
        this.invites = invites;
        this.total = total;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return user.equals(that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getRequests() {
        return requests;
    }

    public void setRequests(Long requests) {
        this.requests = requests;
    }

    public Long getInvites() {
        return invites;
    }

    public void setInvites(Long invites) {
        this.invites = invites;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

}
