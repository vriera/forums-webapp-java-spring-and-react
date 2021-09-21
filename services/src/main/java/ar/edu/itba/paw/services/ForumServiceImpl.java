package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistance.ForumDao;
import ar.edu.itba.paw.interfaces.services.ForumService;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Forum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ForumServiceImpl implements ForumService {

    @Autowired
    private ForumDao forumDao;

    @Override
    public List<Forum> list(){
        return forumDao.list();
    }

    @Override
    public List<Forum> findByCommunity(Number communityId){
        return forumDao.findByCommunity(communityId);
    }

    @Override
    public Optional<Forum> findById(Number forumId) {
        if(forumId == null || forumId.longValue() <= 0)
            return Optional.empty();

        return forumDao.findById(forumId);
    }


}
