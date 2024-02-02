package ar.edu.itba.paw.interfaces.persistance;

import ar.edu.itba.paw.models.*;

import java.util.List;

public interface SearchDao {
   List<Question> search(String query , SearchFilter filter , SearchOrder order , Number community , User user, int limit , int page);
   List<Question> search( SearchFilter filter , SearchOrder order , Long community , User user , int limit , int page);
   List<User> searchUser(String query, int page , int limit);
   List<Community> searchCommunity(String query, int page , int limit);
   List<Answer> getTopAnswers(Long userId);
   Integer searchCount(String query , SearchFilter filter , Long community , User user);
   Integer searchCount(SearchFilter filter , Long community , User user);
   Integer searchUserCount(String query );
   Integer  searchCommunityCount(String query );
}
