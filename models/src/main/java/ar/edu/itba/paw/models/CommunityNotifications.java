package ar.edu.itba.paw.models;

public class CommunityNotifications {
    private Community community;
    private Long notifications;

    public CommunityNotifications(){}

    public CommunityNotifications(Community community , Long notifications) {
        this.community = community;
        this.notifications = notifications;
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
