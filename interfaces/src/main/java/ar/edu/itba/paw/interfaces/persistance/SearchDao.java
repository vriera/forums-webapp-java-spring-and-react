package ar.edu.itba.paw.interfaces.persistance;

import ar.edu.itba.paw.models.Answer;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Question;
import ar.edu.itba.paw.models.User;

import java.util.List;

public interface SearchDao {
   List<Question> search(String query , Number Filter , Number Order , Number community , User user);
   List<Question> search( Number filter , Number order , Number community , User user);
   List<User> searchUser(String query);
   List<Community> searchCommunity(String query);
   List<Answer> getTopAnswers();
}
