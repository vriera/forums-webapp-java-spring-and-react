package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistance.QuestionDao;
import ar.edu.itba.paw.interfaces.services.ForumService;
import ar.edu.itba.paw.interfaces.services.ImageService;
import ar.edu.itba.paw.interfaces.services.QuestionService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.*;

@Service
public class QuestionServiceImpl implements QuestionService {
    //TODO LIMPIAR EL QUESTION SERVICE
    @Autowired
    private ImageService imageService;
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
    public Optional<Question> create(String title , String body , User owner, Forum forum , byte[] image){
        if(title == null || title.isEmpty() || body == null || body.isEmpty() || owner == null || forum == null)
            return Optional.empty();
        Number imageId;
        if ( image != null) {
            System.out.println("La foto es null");
            Image imageObj = imageService.createImage(image);
            imageId = imageObj.getImageId();
        }else {
            imageId = null;
        }
        Optional<User> user = userService.findById(owner.getId());
        if ( user.isPresent()){
           return Optional.ofNullable(questionDao.create(title , body , user.get(), forum , imageId));
        }
        else {
            owner = userService.create(owner.getUsername() , owner.getEmail(), owner.getPassword()).orElseThrow(NoSuchElementException::new); //Si tuve un error creando el owner, se rompe
            return  Optional.ofNullable(questionDao.create(title , body , owner, forum, imageId));
        }
    }



    @Override
    public Optional<Question> questionVote(Long idAnswer, Boolean vote, String email) {
        if(idAnswer == null || vote == null || email == null)
            return Optional.empty();
        Optional<Question> q = findById(idAnswer);
        Optional<User> u = userService.findByEmail(email);
        if(!q.isPresent() || !u.isPresent())
            return Optional.empty();

        questionDao.addVote(vote,u.get().getId(),idAnswer);
        return q;
    }

    @Override
    public Optional<Question> create(String title, String body, String ownerEmail, Number forumId , byte[] image){

        Optional<User> owner = userService.findByEmail(ownerEmail);
        Optional<Forum> forum = forumService.findById(forumId.longValue());

        System.out.println("ALGUN TIPO DE ERROR ALGUN TIPO DE ERROR"); //FIXME: mensajes de error poco significativos
        if(!owner.isPresent() || !forum.isPresent())
            return Optional.empty();
        System.out.println("ALGUN TIPO DE ERROR ALGUN TIPO DE ERROR2");
        return create(title, body, owner.get(), forum.get() , image);
    }


}
