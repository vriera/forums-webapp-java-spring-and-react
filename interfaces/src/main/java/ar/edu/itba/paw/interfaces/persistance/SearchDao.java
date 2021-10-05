package ar.edu.itba.paw.interfaces.persistance;

import ar.edu.itba.paw.models.Answer;
import ar.edu.itba.paw.models.Question;

import java.util.List;

public interface SearchDao {
   List<Question> search(String query , Boolean hasAnswers , Boolean noAnswer , Boolean verifiedAnswer );
   List<Question> searchByCommunity(String query, Number communityId);
   List<Answer> getTopAnswers();
}
