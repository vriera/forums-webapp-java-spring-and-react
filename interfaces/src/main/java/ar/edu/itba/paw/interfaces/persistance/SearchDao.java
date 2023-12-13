package ar.edu.itba.paw.interfaces.persistance;

import ar.edu.itba.paw.models.*;

import java.util.List;

public interface SearchDao {
   List<Question> search(String query, SearchFilter filter, SearchOrder order, Long communityId, long userId, int limit,
         int offset);

   List<Question> search(SearchFilter filter, SearchOrder order, Long communityId, long userId, int limit, int offset);

   List<User> searchUser(String query, int limit, int offset);

   List<Community> searchCommunity(String query, int limit, int offset);

   List<Answer> getTopAnswers(Number userId);

   long searchCount(String query, SearchFilter filter, Long communityId, long userId);

   long searchCount(SearchFilter filter, Long communityId, long userId);

   long searchUserCount(String query);

   long searchCommunityCount(String query);
}
