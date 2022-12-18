package ar.edu.itba.paw.webapp.controller.utils;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class PaginationHeaderUtils {
    public static Response addPaginationLinks(int page , int maxPage , UriBuilder uriBuilder , Response.ResponseBuilder responseBuilder){

        if(maxPage == 0){
            return Response.noContent().build();
        }
        responseBuilder.link(uriBuilder.clone().queryParam("page" , 1).build(), "first");
        responseBuilder.link(uriBuilder.clone().queryParam("page", maxPage).build(), "last");
       if(maxPage > 1){
            if(page > 1 && page <= maxPage ) { //if its bigger there is no previous
                responseBuilder.link(uriBuilder.clone().queryParam("page", page -1).build(), "prev");
            }
           if(page >= 1 && page < maxPage ) { //if its bigger there is no previous
               responseBuilder.link(uriBuilder.clone().queryParam("page", page +1).build(), "next");
           }
       }
       return responseBuilder.build();
    }
}
