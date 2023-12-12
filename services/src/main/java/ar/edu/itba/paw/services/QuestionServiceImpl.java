package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistance.QuestionDao;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.services.utils.PaginationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class QuestionServiceImpl implements QuestionService {

    private static final int PAGE_SIZE = PaginationUtils.PAGE_SIZE;

    @Autowired
    private ImageService imageService;

    @Autowired
    private QuestionDao questionDao;


    @Autowired
    private CommunityService communityService;

    @Autowired
    private ForumService forumService;

    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionServiceImpl.class);



    @Override
    public QuestionVotes getQuestionVote(long questionId , long userId) {
      return questionDao.findVote(questionId,userId).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public Question findById(long id ) {
        return questionDao.findById(id).orElseThrow(NoSuchElementException::new);
    }

    @Override
    @Transactional
    public Question create(String title , String body , User owner, long communityId, byte[] image){
        if(title == null || title.isEmpty() || body == null || body.isEmpty() || owner == null )
            throw new IllegalArgumentException();

        Long imageId;
        if ( image != null && image.length > 0) {
            Image imageObj = imageService.createImage(image);
            imageId = imageObj.getId();
        }else {
            imageId = null;
        }

        Forum forum = forumService.findByCommunity(communityId).stream().findFirst().orElseThrow(IllegalArgumentException::new);
        //TODO: MAP ERROR
        // if (!f.isPresent()) {
        //    return GenericResponses.badRequest("forum.not.found",
        //            "A forum for the given community has not been found");
        // }

        return questionDao.create(title , body , owner, forum, imageId);
    }

    @Override
    public Boolean questionVote(long questionId, Boolean vote, User  user) {

        if(user == null){
            LOGGER.warn("Question ({}) or email ({}) is null", questionId, user);
            return false;
        }

        Question question = this.findById(questionId);

        //Si no tiene acceso a la comunidad, no quiero que pueda votar
        if(communityService.canAccess(user.getId() , question.getForum().getCommunity().getId())){
            questionDao.addVote(vote, user, questionId);
            return true;
        }
        return false;
    }


    @Override
    public List<QuestionVotes> findVotesByQuestionId(long questionId , Long userId , int page){
        if(!(userId == null || userId <0)){
            List<QuestionVotes> questionVotesList = new ArrayList<>();
            if(page == 0) {
                try {
                    questionVotesList.add(getQuestionVote(questionId, userId));
                }catch (NoSuchElementException ignored){}
            }
            return  questionVotesList;
        }
        return questionDao.findVotesByQuestionId(questionId, PAGE_SIZE , page*PAGE_SIZE);
    };

    @Override
    public long findVotesByQuestionIdPagesCount(long questionId, Long userId){
        if(!(userId == null || userId <0)){
            try {
                getQuestionVote(questionId , userId);
                return 1;
            }catch (NoSuchElementException ignored){}
            return 0;
        }
        long count = questionDao.findVotesByQuestionIdCount(questionId);
        return PaginationUtils.getPagesFromTotal(count);
    }




}
