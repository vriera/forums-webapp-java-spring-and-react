package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistance.QuestionDao;
import ar.edu.itba.paw.interfaces.services.QuestionService;
import ar.edu.itba.paw.interfaces.services.SearchService;
import ar.edu.itba.paw.models.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {

	@Autowired
	QuestionDao questionDao;

	@Autowired
	QuestionService questionService;

	@Override
	public List<Question> search(String query) {
		if(query.isEmpty())
			return questionService.findAll();

		return questionDao.search(query);
	}

	@Override
	public List<Question> searchByCommunity(String query, Number communityId) {
		if(query.isEmpty())
			return questionService.findAll();

		return questionDao.searchByCommunity(query, communityId);
	}
}
