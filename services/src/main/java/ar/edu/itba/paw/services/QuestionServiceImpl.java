package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistance.QuestionDao;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl implements QuestionService {
    @Autowired
    private ImageService imageService;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserService userService;

    @Autowired
    private CommunityService communityService;

    @Autowired
    private ForumService forumService;

    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionServiceImpl.class);

    @Override
    public List<Question> findAll(User requester, int limit, int offset){

        return questionDao.findAll(limit, offset).stream().filter(question -> communityService.canAccess(requester, question.getCommunity())).collect(Collectors.toList());
    }

    @Override
    public Optional<Question> findById(User requester,long id ){
        Optional<Question> maybeQuestion = questionDao.findById(id);

        if(maybeQuestion.isPresent() && !communityService.canAccess(requester, maybeQuestion.get().getForum().getCommunity()))
            return Optional.empty();

        return maybeQuestion;
    }

    @Override
    public List<Question> findByForum(User requester, Number community_id, Number forum_id, int limit, int offset){
        if(community_id == null){
            return Collections.emptyList();
        }

        if(forum_id == null){
            Optional<Forum> maybeForum= forumService.findByCommunity(community_id).stream().findFirst();
            if(!maybeForum.isPresent()){
                return Collections.emptyList();
            }
            if(!communityService.canAccess(requester, maybeForum.get().getCommunity()))
                return Collections.emptyList();

            forum_id = maybeForum.get().getId();
        }

        return questionDao.findByForum(community_id, forum_id, limit, offset);
    }

    @Override
    @Transactional
    public Optional<Question> create(String title , String body , User owner, Forum forum , byte[] image){
        if(title == null || title.isEmpty() || body == null || body.isEmpty() || owner == null || forum == null)
            return Optional.empty();
        Long imageId;
        if ( image != null && image.length > 0) {
            System.out.println("La foto es null");
            Image imageObj = imageService.createImage(image);
            imageId = imageObj.getId();
        }else {
            imageId = null;
        }

        //Si no tiene acceso a la comunidad, no quiero que pueda preguntar
        if(!communityService.canAccess(owner, forum.getCommunity()))
            return Optional.empty();

        return Optional.ofNullable(questionDao.create(title , body , owner, forum , imageId));
    }



    @Override
    public Optional<Question> questionVote(Long idAnswer, Boolean vote, String email) {
        if(idAnswer == null || vote == null || email == null)
            return Optional.empty();
        Optional<User> u = userService.findByEmail(email);
        Optional<Question> q = findById(u.orElse(null), idAnswer);

        if(!q.isPresent() || !u.isPresent())
            return Optional.empty();

        if(!communityService.canAccess(u.get(), q.get().getCommunity())) //Si no tiene acceso a la comunidad, no quiero que pueda votar
            return Optional.empty();

        questionDao.addVote(vote,u.get().getId(),idAnswer);
        return q;
    }

    @Override
    @Transactional
    public Optional<Question> create(String title, String body, String ownerEmail, Number forumId , byte[] image){

        Optional<User> owner = userService.findByEmail(ownerEmail);
        Optional<Forum> forum = forumService.findById(forumId.longValue());

        if(!owner.isPresent() || !forum.isPresent())
            return Optional.empty();

        return create(title, body, owner.get(), forum.get() , image);
    }


}
