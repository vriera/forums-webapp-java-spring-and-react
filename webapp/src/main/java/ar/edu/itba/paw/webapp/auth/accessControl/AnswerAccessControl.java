package ar.edu.itba.paw.webapp.auth.accessControl;

import ar.edu.itba.paw.interfaces.services.AnswersService;
import ar.edu.itba.paw.interfaces.services.QuestionService;
import ar.edu.itba.paw.models.Answer;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.controller.Commons;
import javassist.NotFoundException;
import jdk.nashorn.internal.parser.JSONParser;
import netscape.javascript.JSObject;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.NoSuchElementException;
import java.util.Optional;

@Component
public class AnswerAccessControl {


    @Autowired
    private AnswersService as;

    @Autowired
    private QuestionService qs;

    @Autowired
    private AccessControl ac;

    @Autowired
    private CommunityAccessControl cas;

    @Autowired
    private Commons commons;


    @Transactional(readOnly = true)
    public boolean canAsk(HttpServletRequest request) throws NoSuchElementException {
        try {
            System.out.println("Content: " + request.getContentType());
            String body = extractBodyAsString(request);
            System.out.println("body: " + body);
            JSONObject object = new JSONObject(body);
            System.out.println("questionId: " + object.get("questionId"));
            long id = object.getLong("questionId");
            return canAccess(id);
        }catch (Exception e){
            System.out.println("pinchó");
            return true;
        }
    }
    @Transactional(readOnly = true)
    public boolean canAccess(long answerId) throws NoSuchElementException{
        return canAccess(commons.currentUser() , answerId);
    }

    @Transactional(readOnly = true)
    public boolean canAccess(long userId, long answerId ) throws NoSuchElementException{
       return canAccess(ac.checkUser(userId) ,answerId);
    }

    @Transactional(readOnly = true)
    public boolean canAccess(User u , long answerId) throws NoSuchElementException{

        Answer answer = as.findById(answerId);
        return cas.canAccess(u , answer.getQuestion().getForum().getCommunity().getId());
    }

    @Transactional(readOnly = true)
    public boolean canVerify( long answerId) {
        User u = commons.currentUser();
        Answer a = as.findById(answerId);

        Question q = qs.findById( a.getQuestion().getId());

        return u != null && u.getId() == q.getOwner().getId();
    }

    private String extractBodyAsString(HttpServletRequest request) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
            char[] charBuffer = new char[8 * 1024];
            int bytesRead;
            while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                stringBuilder.append(charBuffer, 0, bytesRead);
            }
        }
        return stringBuilder.toString();
    }

}
