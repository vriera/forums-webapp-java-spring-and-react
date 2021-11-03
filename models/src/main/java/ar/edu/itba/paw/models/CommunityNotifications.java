package ar.edu.itba.paw.models;

import jdk.nashorn.internal.ir.annotations.Immutable;

import javax.persistence.*;

public class CommunityNotifications {

    private Community community;

    private User moderator;

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
