package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.controller.dto.CommunityListDto;
import ar.edu.itba.paw.webapp.controller.dto.cards.CommunityCardDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.stream.Collectors;
@Component
@Path("community-cards")
public class CommunityCardController {

    @Autowired
    private SearchService ss;

    @Context
    private UriInfo uriInfo;

    @Autowired
    private Commons commons;

    @GET
    @Path("")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response list(@DefaultValue("0") @QueryParam("page") int page, @DefaultValue("5") @QueryParam("size") int size , @DefaultValue("") @QueryParam("query") String query) {
        int offset = (page) * size;
        if(size < 1 )
            size = 1;
        int limit = size;
        User u = commons.currentUser();
        List<Community> cl = ss.searchCommunity(query , limit, offset);
        long total = (long) Math.ceil(ss.searchCommunityCount(query) / (double)size);
//        CommunityListDto cld = CommunityListDto.communityListToCommunityListDto(cl, uriInfo, query, page, size,total);
        List<CommunityCardDto> clDto = cl.stream().map( x -> CommunityCardDto.toCommunityPreview(x , uriInfo) ).collect(Collectors.toList());
        return Response.ok(
                new GenericEntity<List<CommunityCardDto>>(clDto) {
                }
        ).build();
    }

}
