package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistance.QuestionDao;
import ar.edu.itba.paw.interfaces.services.ForumService;
import ar.edu.itba.paw.interfaces.services.QuestionService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Forum;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.*;

@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserService userService;

    @Autowired
    private ForumService forumService;

    private Integer nextKey = 0;
    private final HashMap<Integer, Question> temporaryQuestions = new HashMap<>();


    @Override
    public List<Question> findAll(){
        return questionDao.findAll();
    }

    @Override
    public Optional<Question> findById(long id ){
        return questionDao.findById(id);
    }

    @Override
    public List<Question> findByForum(Number community_id, Number forum_id){
        if(community_id == null){
            return Collections.emptyList();
        }

        if(forum_id == null){
            Optional<Forum> maybeForum= forumService.findByCommunity(community_id).stream().findFirst();
            if(!maybeForum.isPresent()){
                return Collections.emptyList();
            }
            forum_id = maybeForum.get().getId();
        }

        return questionDao.findByForum(community_id, forum_id);
    }

    @Override
    public Optional<Question> create(String title , String body , User owner, Forum forum){
        if(title == null || title.isEmpty() || body == null || body.isEmpty() || owner == null || forum == null)
            return Optional.empty();

        Optional<User> user = userService.findById(owner.getId());

        if ( user.isPresent()){
           return Optional.ofNullable(questionDao.create(title , body , user.get(), forum));
        }
        else {
            owner = userService.create(owner.getUsername() , owner.getEmail(), owner.getPassword()).orElseThrow(NoSuchElementException::new); //Si tuve un error creando el owner, se rompe
            return  Optional.ofNullable(questionDao.create(title , body , owner, forum));
        }
    }

    @Override
    public Optional<Question> create(Question question){
        return create(question.getTitle() , question.getBody() , question.getOwner()  , question.getForum());
    }

    @Override
    public Integer addTemporaryQuestion( String title , String body, Number community , Number forum ){
        temporaryQuestions.put(nextKey , new Question(title,body,community.longValue() ,forum.longValue() ));
        return nextKey++;
    }
    @Override
    public Optional<Question> removeTemporaryQuestion( Integer key , String name ,String email){
        Question aux = temporaryQuestions.get(key);
        aux.setOwner(new User( name , email));
        Optional<Question> q = create(aux);
        if ( q.isPresent() ) {
            temporaryQuestions.remove(key);
        }
        return q;
    }
}
