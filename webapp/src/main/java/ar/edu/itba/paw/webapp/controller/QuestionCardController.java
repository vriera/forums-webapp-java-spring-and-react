package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.CommunityService;
import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.*;

import ar.edu.itba.paw.webapp.controller.dto.cards.QuestionCardDto;
import ar.edu.itba.paw.webapp.controller.utils.GenericResponses;
import ar.edu.itba.paw.webapp.controller.utils.PaginationHeaderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//TODO: ELIMINAR ESTA CLASE URGENTE!
@Component
@Path("question-cards")
public class QuestionCardController {
    private final static int PAGE_SIZE = 10;
    @Context
    private UriInfo uriInfo;

    @Autowired
    private SearchService ss;

    @Autowired
    private CommunityService cs;
    @Autowired
    private UserService us;
    @Autowired
    private Commons commons;


}
