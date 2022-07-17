package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.interfaces.services.CommunityService;
import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.webapp.auth.PawUserDetailsService;
import ar.edu.itba.paw.webapp.controller.utils.AuthenticationUtils;
import ar.edu.itba.paw.webapp.dto.CommunityDto;
import ar.edu.itba.paw.webapp.dto.CommunityListDto;
import ar.edu.itba.paw.webapp.dto.CommunitySearchDto;
import ar.edu.itba.paw.webapp.dto.KarmaDto;
import ar.edu.itba.paw.webapp.form.PaginationForm;
import com.sun.tracing.dtrace.ProviderAttributes;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Optional;

@Component
@Path("community")
public class CommunityController {

    @Autowired
    private CommunityService cs;

    @Autowired
    private UserService us;

    @Autowired
    private SearchService ss;

    @Autowired
    private PawUserDetailsService userDetailsService;

    @Context
    private UriInfo uriInfo;

    @Autowired
    private Commons commons;


    @GET
    @Path("/list")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response list(@DefaultValue("1") @QueryParam("page") int page, @DefaultValue("10") @QueryParam("size") int size) {
        int offset = (page - 1) * size;
        int limit = size;
        User u = commons.currentUser();

        List<Community> cl = cs.list(u);
        CommunityListDto cld = CommunityListDto.CommunityListToCommunityListDto(cl, uriInfo, null, page, size, cl.size());
        return Response.ok(
                new GenericEntity<CommunityListDto>(cld) {
                }
        ).build();
    }

    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getCommunity(@PathParam("id") int id) {
        User u = commons.currentUser();

        Optional<Community> c = cs.findById(id);

        if (!cs.canAccess(u, c.orElse(null))) {
            return Response.status(403).build();
        }

        CommunityDto cd = CommunityDto.communityToCommunityDto(c.orElse(null), uriInfo);

        return Response.ok(
                new GenericEntity<CommunityDto>(cd) {
                }
        ).build();
    }

    @GET
    @Path("/view/all/")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response allPost(
            @DefaultValue("") @QueryParam("query") String query,
            @DefaultValue("0") @QueryParam("filter") int filter,
            @DefaultValue("0") @QueryParam("order") int order,
            @DefaultValue("1") @QueryParam("page") int page,
            @DefaultValue("10") @QueryParam("size") int size
    ) {
        //NO SE SI EL SIZE me puede romper el back!
        //@ModelAttribute("paginationForm") PaginationForm paginationForm)

        int offset = (page - 1) * size;
        int limit = size;

        User u = commons.currentUser();

        List<Question> questionList = ss.search(query, SearchFilter.values()[filter], SearchOrder.values()[order], -1, u, limit, offset);
        int questionCount = ss.countQuestionQuery(query, SearchFilter.values()[filter], SearchOrder.values()[order], -1, u);

        int pages = (int) Math.ceil((double) questionCount / size);

        CommunitySearchDto csDto = CommunitySearchDto.QuestionListToCommunitySearchDto(questionList, uriInfo, -1, query, filter, order, page, size, pages);
        return Response.ok(

                new GenericEntity<CommunitySearchDto>(csDto) {
                }

        ).build();
    }

    /*
    @RequestMapping("/community/search")
    public ModelAndView searchCommunity(@RequestParam(value = "query" , required = false , defaultValue = "") String query,
                                        @ModelAttribute("paginationForm") PaginationForm paginationForm){
        ModelAndView mav = new ModelAndView("search/community");
        Optional<User> maybeUser = AuthenticationUtils.authorizeInView(mav , us );
        //mav.addObject("communityList" , cs.list(maybeUser.orElse(null)));
        int countCommunity = ss.searchCommunityCount(query);
        mav.addObject("communitySearchList" , ss.searchCommunity(query ,paginationForm.getLimit(), paginationForm.getLimit()*(paginationForm.getPage() - 1)));
        mav.addObject("count",(Math.ceil((double)((int)countCommunity)/ paginationForm.getLimit())));
        mav.addObject("currentPage",paginationForm.getPage());
        mav.addObject("communityList" , cs.list(maybeUser.orElse(null)));
        mav.addObject("query", query);
        return mav;
    }*/

    @GET
    @Path("/view/{id}")
    @Produces(value = {MediaType.APPLICATION_JSON})
    public Response singleCommunity(@PathParam("id") int id,
                                    @DefaultValue("") @QueryParam("query") String query,
                                    @DefaultValue("0") @QueryParam("filter") int filter,
                                    @DefaultValue("0") @QueryParam("order") int order,
                                    @DefaultValue("1") @QueryParam("page") int page,
                                    @DefaultValue("10") @QueryParam("size") int size) {

        int offset = (page - 1) * size;
        int limit = size;
        User u = commons.currentUser();
        List<Question> questionList = ss.search(query, SearchFilter.values()[filter], SearchOrder.values()[order], id, u, limit, offset);

        int questionCount = ss.countQuestionQuery(query, SearchFilter.values()[filter], SearchOrder.values()[order], id, u);
        int pages = (int) Math.ceil((double) questionCount / size);

        CommunitySearchDto csDto = CommunitySearchDto.QuestionListToCommunitySearchDto(questionList, uriInfo, id, query, filter, order, page, size, pages);
        return Response.ok(

                new GenericEntity<CommunitySearchDto>(csDto) {
                }

        ).build();
    }


    @POST
    @Produces(value = {MediaType.APPLICATION_JSON})
    @Consumes(value = {MediaType.APPLICATION_JSON})
    public Response create(String body) {
        final User u = commons.currentUser();
        //Name
        //Description
        final JSONObject params = new JSONObject(body);
        //TODO: the validations
        final String title = params.getString("title");
        final String description = params.getString("description");

        Optional<Community> c = cs.create(title, description, u);

        if (!c.isPresent()) {
            return Response.serverError().build();
        }

        return Response.ok(

                new GenericEntity<CommunityDto>(CommunityDto.communityToCommunityDto(c.get(), uriInfo)) {
                }
        ).build();

    }

}



