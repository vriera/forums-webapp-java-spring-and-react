package ar.edu.itba.paw.interfaces.persistance;

import ar.edu.itba.paw.models.Question;

import java.util.List;

public interface SearchDao {
   List<Question> search(String query);

}
