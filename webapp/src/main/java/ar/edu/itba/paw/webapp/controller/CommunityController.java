package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.interfaces.services.CommunityService;
import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.SearchFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("community")
@Component
public class CommunityController {
    @Autowired
    private CommunityService cs;
    @Autowired
    private SearchService ss;
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneralController.class);

    @GET
    @Produces(value = { MediaType.APPLICATION_JSON })
    public Response getCommunities(@QueryParam("page") @DefaultValue("1") int page){
        return Response.ok().build();
    }
}
