package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistance.QuestionDao;
import ar.edu.itba.paw.interfaces.persistance.SearchDao;
import ar.edu.itba.paw.interfaces.services.QuestionService;
import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.models.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {
	@Autowired
	SearchDao searchDao;

	@Autowired
	QuestionDao questionDao;

	@Autowired
	QuestionService questionService;

	@Override
	public List<Question> search(String query) {
		if(query == null || query.isEmpty())
			return questionService.findAll();

		return searchDao.search(query);
	}

	@Override
	public List<Question> searchByCommunity(String query, Number communityId) {

		if(communityId == null)
			return Collections.emptyList();

		if(query == null || query.isEmpty())
			return questionService.findByForum(communityId, null); //Esto es seguro porque si es null, levanta el primer foro
		//FIXME: esto es temporal, porque no tenemos foros realmente y este método necesita un forum_id

		return questionDao.searchByCommunity(query, communityId);
	}
}
