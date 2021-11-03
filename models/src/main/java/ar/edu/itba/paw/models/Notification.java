package ar.edu.itba.paw.models;

import jdk.nashorn.internal.ir.annotations.Immutable;
import javax.persistence.*;

@Entity
@Immutable
@Table(name="notifications")
public class Notification {

    @Id
    @Column(name="user_id")
    private Long id;

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
