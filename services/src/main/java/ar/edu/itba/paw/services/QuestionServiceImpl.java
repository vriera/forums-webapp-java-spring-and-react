package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.exceptions.GenericBadRequestException;
import ar.edu.itba.paw.interfaces.persistance.QuestionDao;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.Forum;
import ar.edu.itba.paw.models.Image;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;
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
    public Optional<Question> findById(User requester,long id ) {
        Optional<Question> maybeQuestion = questionDao.findById(id);

        maybeQuestion.ifPresent(question -> question.getQuestionVote(requester));

        return maybeQuestion;
    }

    @Override
    public List<Question> findByUser(Long userId, int page, int limit) {
        return questionDao.findByUser(userId,page,limit);
    }

    @Override
    public int findByUserCount(Long userId) {
        return questionDao.findByUserCount(userId);
    }

    @Override
    public Optional<Question> findByIdWithoutVotes(long id ) {
        return questionDao.findById(id);

    }

    @Override
    public List<Question> findByForum(User requester, Number communityId, Number forumId, int limit, int offset){
        if(communityId == null){
            return Collections.emptyList();
        }

        if(forumId == null){
            Optional<Forum> maybeForum= forumService.findByCommunity(communityId).stream().findFirst();
            if(!maybeForum.isPresent()){
                return Collections.emptyList();
            }
            if(!communityService.canAccess(requester, maybeForum.get().getCommunity()))
                return Collections.emptyList();

            forumId= maybeForum.get().getId();
        }

        return questionDao.findByForum(communityId, forumId, limit, offset);
    }

    @Override
    @Transactional
    public Optional<Question> create(String title , String body , User owner, Long communityId , byte[] image) throws GenericBadRequestException {
        if(title == null || title.isEmpty() || body == null || body.isEmpty() || owner == null || communityId == null) throw new GenericBadRequestException("the question form is wrong","bad.form");
        Long imageId;
        if ( image != null && image.length > 0) {

            Image imageObj = imageService.createImage(image);
            imageId = imageObj.getId();
        }else {
            LOGGER.debug("La foto no null");
            imageId = null;
        }

        Optional<Forum> forum = forumService.findByCommunity(communityId).stream().findFirst();
        if (!forum.isPresent()) throw new GenericBadRequestException("forum.not.found", "A forum for the given community has not been found");


        return Optional.ofNullable(questionDao.create(title , body , owner, forum.get() , imageId));
    }

/*    @Override
    @Transactional
    public Boolean addImage(User u , Long questionId ,byte[] data){
       if(!findById(u , questionId).isPresent())
           return false;

       Image i = imageService.createImage(data);

      return questionDao.updateImage(questionId,i.getId()).isPresent();
    }*/

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




}
