package ar.edu.itba.paw.models;

public class Notification {

    private User user;
    private Long requests;
    private Long invites;
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
