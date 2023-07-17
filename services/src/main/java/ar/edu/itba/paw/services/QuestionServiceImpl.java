package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistance.QuestionDao;
import ar.edu.itba.paw.interfaces.services.*;
import ar.edu.itba.paw.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final static int PAGE_SIZE = 10;

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
    public QuestionVotes getQuestionVote(Long questionId ,Long userId) {
        Question q = questionDao.findById(questionId).orElseThrow(NoSuchElementException::new);
        return q.getQuestionVotes().stream().filter(x->x.getOwner().getId() == userId).findFirst().orElseThrow(NoSuchElementException::new);
    }

    @Override
    public Question findById(long id ) {
        return questionDao.findById(id).orElseThrow(NoSuchElementException::new);
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

            forumId = maybeForum.get().getId();
        }

        return questionDao.findByForum(communityId, forumId, limit, offset);
    }

    @Override
    @Transactional
    public Question create(String title , String body , User owner, Forum forum , byte[] image){
        if(title == null || title.isEmpty() || body == null || body.isEmpty() || owner == null || forum == null)
            throw new IllegalArgumentException();
        Long imageId;
        if ( image != null && image.length > 0) {

            Image imageObj = imageService.createImage(image);
            imageId = imageObj.getId();
        }else {
            LOGGER.debug("La foto no null");
            imageId = null;
        }
//TODO:handeleado por security
//        //Si no tiene acceso a la comunidad, no quiero que pueda preguntar
//        if(!communityService.canAccess(owner, forum.getCommunity()))
//            return Optional.empty();

        //HANDLE 500?
        return questionDao.create(title , body , owner, forum , imageId);
    }

    @Override
    public Boolean questionVote(Question question, Boolean vote, User  user) {

        if(question == null || user == null){
            LOGGER.warn("Question ({}) or email ({}) is null", question, user);
            return false;
        }

        //Si no tiene acceso a la comunidad, no quiero que pueda votar
        if(communityService.canAccess(user , question.getForum().getCommunity())) {
            questionDao.addVote(vote, user, question.getId());
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public Question create(String title, String body, User user, Integer forumId , byte[] image){

        Forum forum = forumService.findById(forumId.longValue()).orElseThrow(NoSuchElementException::new);


        return create(title, body, user, forum , image);
    }




    @Override
    public List<QuestionVotes> findVotesByQuestionId(Long questionId , Long userId , int page){
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
    public int findVotesByQuestionIdCount(Long questionId, Long userId){
        if(!(userId == null || userId <0)){
            try {
                getQuestionVote(questionId , userId);
                return 1;
            }catch (NoSuchElementException ignored){}
            return 0;
        }
        int count = questionDao.findVotesByQuestionIdCount(questionId);

        int mod = count % PAGE_SIZE;
        return mod != 0 ? (count / PAGE_SIZE) + 1 : count / PAGE_SIZE;
    }




}
