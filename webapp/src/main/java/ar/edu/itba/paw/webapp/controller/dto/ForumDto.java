package ar.edu.itba.paw.webapp.controller.dto;

import ar.edu.itba.paw.models.Forum;

import javax.ws.rs.core.UriInfo;

public class ForumDto {

    private Long id;
    private String name;
    private CommunityDto community;
    private String url;

    public static ForumDto forumToForumDto(Forum f, UriInfo uri){
        ForumDto forumDto = new ForumDto();
        forumDto.community = CommunityDto.communityToCommunityDto(f.getCommunity(),uri);
        forumDto.name = f.getName();
        forumDto.url = uri.getBaseUriBuilder().path("/forum/").path(String.valueOf(f.getId())).build().toString();
        return forumDto;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCommunity(CommunityDto community) {
        this.community = community;
    }

    public CommunityDto getCommunity() {
        return community;
    }



    @Override
    public String toString() {
        return "ForumDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", community=" + community +
                ", url='" + url + '\'' +
                '}';
    }
}
