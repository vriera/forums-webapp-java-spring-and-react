package ar.edu.itba.paw.models;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import javax.persistence.*;

@Entity
@Immutable
@Subselect("SELECT * FROM community_notifications")
public class CommunityNotifications {

    @Id
    @OneToOne
    @JoinColumn(name = "community_id")
    private Community community;

    @OneToOne
    @JoinColumn(name = "moderator_id")
    private User moderator;

    @Column(name="requests")
    private Long notifications;

    public CommunityNotifications(){}

    public CommunityNotifications(Community community , Long notifications) {
        this.community = community;
        this.notifications = notifications;
    }

    public User getModerator() {
        return moderator;
    }

    public void setModerator(User moderator) {
        this.moderator = moderator;
    }

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }

    public Long getNotifications() {
        return notifications;
    }

    public void setNotifications(Long notifications) {
        this.notifications = notifications;
    }

}
