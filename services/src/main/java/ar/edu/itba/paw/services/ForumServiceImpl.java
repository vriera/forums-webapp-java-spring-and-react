package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistance.ForumDao;
import ar.edu.itba.paw.interfaces.services.ForumService;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Forum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ForumServiceImpl implements ForumService {

    @Autowired
    private ForumDao forumDao;

    @Override
    public List<Forum> findByCommunity(long communityId) {
        return forumDao.findByCommunity(communityId);
    }

    @Override
    public Forum findById(long forumId) {
        if (forumId <= 0)
            throw new IllegalArgumentException();

        return forumDao.findById(forumId).orElseThrow(NoSuchElementException::new);
    }

    @Override
    @Transactional
    public Forum create(Community community) {
        return forumDao.create(community);
    }

}
