package ar.edu.itba.paw.interfaces.persistance;

import ar.edu.itba.paw.models.*;

import java.util.List;

public interface SearchDao {
   List<Question> search(String query , SearchFilter Filter , SearchOrder Order , Number community , User user, int limit , int offset);
   List<Question> search( SearchFilter filter , SearchOrder order , Number community , User user , int limit , int offset);
   List<User> searchUser(String query, int limit , int offset);
   List<Community> searchCommunity(String query, int limit , int offset);
   List<Answer> getTopAnswers(Number userId);
}
