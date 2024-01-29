package ar.edu.itba.paw.interfaces.persistance;

import ar.edu.itba.paw.models.*;

import java.util.List;

public interface SearchDao {
   List<Question> search(String query , SearchFilter filter , SearchOrder order , Number community , User user, int limit , int offset);
   List<Question> search( SearchFilter filter , SearchOrder order , Number community , User user , int limit , int offset);
   List<User> searchUser(String query, int page , int limit);
   List<Community> searchCommunity(String query, int page , int limit);
   List<Answer> getTopAnswers(Number userId);
   Number searchCount(String query , SearchFilter filter , Number community , User user);
   Number searchCount(SearchFilter filter , Number community , User user);
   Number searchUserCount(String query );
   Number searchCommunityCount(String query );
}
