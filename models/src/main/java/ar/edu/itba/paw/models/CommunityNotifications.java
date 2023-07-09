package ar.edu.itba.paw.models;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Immutable
@Subselect("SELECT * FROM community_notifications")
public class CommunityNotifications implements Serializable{

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommunityNotifications that = (CommunityNotifications) o;
        return community.equals(that.community);
    }

    @Override
    public int hashCode() {
        return Objects.hash(community);
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
