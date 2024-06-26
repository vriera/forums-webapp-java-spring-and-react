package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.models.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.Arrays;

@Component
@Path("images")
public class ImagesController {

    @Context
    private UriInfo uriInfo;

    @Autowired
    private ImageService is;

    @GET
    @Path("/{id}/")
    @Produces({ "image/png", "image/jpeg", "image/gif", MediaType.APPLICATION_JSON })
    public Response images(@PathParam("id") final Long id, @Context Request request) {
        final Image img = is.getImage(id);

        return sendWithCache(img.getImage(), request);
    }

    private Response sendWithCache(final byte[] image, final Request request) {
        CacheControl cacheControl = new CacheControl();

        cacheControl.setMaxAge(60000);
        cacheControl.getCacheExtension().put("public", "");
        cacheControl.getCacheExtension().put("immutable", "");
        cacheControl.setNoTransform(true);
        cacheControl.setMustRevalidate(true);

        EntityTag tag = new EntityTag(Integer.toString(Arrays.hashCode(image)));
        Response.ResponseBuilder responseBuilder = request.evaluatePreconditions(tag);
        if (responseBuilder == null) {
            responseBuilder = Response.ok().entity(image).tag(tag);
        }
        return responseBuilder.cacheControl(cacheControl).build();
    }

}
