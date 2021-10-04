package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistance.QuestionDao;
import ar.edu.itba.paw.interfaces.services.QuestionService;
import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.models.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class SearchServiceImpl implements SearchService {

	@Autowired
	QuestionDao questionDao;

	@Autowired
	QuestionService questionService;

	@Override
	public List<Question> search(String query, int limit, int offset) {
		if(query == null || query.isEmpty())
			return questionService.findAll(limit, offset);

		return questionDao.search(query, limit, offset);
	}

	@Override
	public List<Question> searchByCommunity(String query, Number communityId, int limit, int offset) {

		if(communityId == null)
			return Collections.emptyList();

		if(query == null || query.isEmpty())
			return questionService.findByForum(communityId, null, limit, offset); //Esto es seguro porque si es null, levanta el primer foro
		//FIXME: esto es temporal, porque no tenemos foros realmente y este método necesita un forum_id

		return questionDao.searchByCommunity(query, communityId, limit, offset);
	}

	@Override
	public Optional<Long> countQuestionByCommunity(String query, Number community_id) {
		if(query == null || query.isEmpty()){
			return questionDao.countQuestionsByCommunity(community_id);
		}
		return questionDao.countQuestionsByCommunity(community_id,query);
	}

	@Override
	public Optional<Long> countQuestionQuery(String query) {
		if(query == null || query.isEmpty()){
			return questionDao.countAllQuestions();
		}
		return questionDao.countQuestionQuery(query);
	}


}
