package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CommunityService;
import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.controller.dto.CommunityListDto;
import ar.edu.itba.paw.webapp.controller.dto.cards.CommunityCardDto;
import ar.edu.itba.paw.webapp.controller.utils.GenericResponses;
import ar.edu.itba.paw.webapp.controller.utils.PaginationHeaderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.stream.Collectors;
@Component
@Path("community-cards")
public class CommunityCardController {

    private final static int PAGE_SIZE = 10;
    @Autowired
    private SearchService ss;

    @Context
    private UriInfo uriInfo;

    @Autowired
    private CommunityService cs;

    @Autowired
    private Commons commons;

    @GET
    @Path("")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response list(@DefaultValue("1") @QueryParam("page") int page,
                         @DefaultValue("") @QueryParam("query") String query) {


        int size = PAGE_SIZE;
        int offset = (page - 1) * size;
        if(size < 1 )
            size = 1;

        int limit = size;
        User u = commons.currentUser();
        List<Community> cl = ss.searchCommunity(query , limit, offset);
        int total = (int) Math.ceil(ss.searchCommunityCount(query) / (double)size);
//        CommunityListDto cld = CommunityListDto.communityListToCommunityListDto(cl, uriInfo, query, page, size,total);
        List<CommunityCardDto> clDto = cl.stream().map( x -> CommunityCardDto.toCommunityPreview(x , uriInfo) ).collect(Collectors.toList());
        UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
        if(!query.equals(""))
            uriBuilder.queryParam("query" , query);

        Response.ResponseBuilder res = Response.ok(
                new GenericEntity<List<CommunityCardDto>>(clDto) {
                }
        );
        return PaginationHeaderUtils.addPaginationLinks(page , total , uriBuilder, res );
    }//TODO NO SE ESTA USANDO EL USUARIO?

    @GET
    @Path("/askable")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response list(@DefaultValue("1") @QueryParam("page") int page, @QueryParam("requestorId") int userId) {


        int size = PAGE_SIZE;
        int offset = (page - 1) * size;
        if(size < 1 )
            size = 1;

        int limit = size;
        User u = commons.currentUser();
        if( u == null)
            return GenericResponses.notAuthorized();
        if( u.getId() != userId)
            return GenericResponses.cantAccess();
        System.out.println("asking for list");
        List<Community> cl = cs.list(u.getId() , limit , offset);
        System.out.println("calculating for the list");
        int total = (int) Math.ceil(cs.listCount(u.getId()) / (double)size);
//        CommunityListDto cld = CommunityListDto.communityListToCommunityListDto(cl, uriInfo, query, page, size,total);
        System.out.println("calculated");
        List<CommunityCardDto> clDto = cl.stream().map( x -> CommunityCardDto.toCommunityPreview(x , uriInfo) ).collect(Collectors.toList());
        Response.ResponseBuilder res = Response.ok(
                new GenericEntity<List<CommunityCardDto>>(clDto) {
                }
        );
        return PaginationHeaderUtils.addPaginationLinks(page , total , uriInfo.getAbsolutePathBuilder() , res );
    }



}
