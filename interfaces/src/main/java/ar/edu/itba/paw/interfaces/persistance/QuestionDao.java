package ar.edu.itba.paw.interfaces.persistance;

import ar.edu.itba.paw.models.Category;
import ar.edu.itba.paw.models.Question;

import java.util.List;
import java.util.Optional;

public interface QuestionDao {

    Optional<Question> findById(Long id );

    List<Question> findAll();

    List<Question> findByCategory(Category category);
}
