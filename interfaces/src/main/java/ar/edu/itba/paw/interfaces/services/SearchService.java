package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Question;

import java.util.List;

public interface SearchService {

	//Busca preguntas similares en todas las comunidades y foros
	List<Question> search(String query);

	//Busca preguntas similares en todos los foros dada una comunidad (identificada por su id)
	List<Question> searchByCommunity(String query, Number communityId);

}
