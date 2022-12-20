package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistance.QuestionDao;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.Forum;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;
import org.hibernate.QueryTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
    public List<Question> findAll(User requester, int page){

        return new ArrayList<>(questionDao.findAll(page)); //saque el can access --> ver como hacer eso de otra forma
    }

    @Override
    public Optional<Question> findById(User requester,long id ){
        Optional<Question> maybeQuestion = questionDao.findById(id);

        //para ver si el usuario voto o no
        maybeQuestion.ifPresent(question -> question.getQuestionVote(requester));

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

            Image imageObj = imageService.createImage(image);
            imageId = imageObj.getId();
        }else {
            LOGGER.debug("La foto no null");
            imageId = null;
        }

        //Si no tiene acceso a la comunidad, no quiero que pueda preguntar
        if(!communityService.canAccess(owner, forum.getCommunity()))
            return Optional.empty();

        return Optional.ofNullable(questionDao.create(title , body , owner, forum , imageId));
    }

    @Override
    @Transactional
    public Boolean addImage(User u , Long questionId ,byte[] data){
       if(!findById(u , questionId).isPresent())
           return false;

       Image i = imageService.createImage(data);

      return questionDao.updateImage(questionId,i.getId()).isPresent();
    }

    @Override
    public Boolean questionVote(Question question, Boolean vote, String email) {
        if(question == null || email == null)
            return null;
        Optional<User> u = userService.findByEmail(email);
        if(u.isPresent()){
            if(!communityService.canAccess(u.get(), question.getCommunity())) //Si no tiene acceso a la comunidad, no quiero que pueda votar
                return false;
            questionDao.addVote(vote,u.get(), question.getId());
            return true;
        }else return null;

    }

    @Override
    @Transactional
    public Optional<Question> create(String title, String body, String ownerEmail, Integer forumId , byte[] image){

        Optional<User> owner = userService.findByEmail(ownerEmail);
        Optional<Forum> forum = forumService.findById(forumId.longValue());

        if(!owner.isPresent() || !forum.isPresent())
            return Optional.empty();

        return create(title, body, owner.get(), forum.get() , image);
    }



}
