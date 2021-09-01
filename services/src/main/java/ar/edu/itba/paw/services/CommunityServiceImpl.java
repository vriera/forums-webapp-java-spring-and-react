package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistance.CommunityDao;
import ar.edu.itba.paw.interfaces.services.CommunityService;
import ar.edu.itba.paw.models.Community;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommunityServiceImpl implements CommunityService {

    @Autowired
    private CommunityDao communityDao;

    @Override
    public List<Community> list(){ return communityDao.list();}

    @Override
    public Optional<Community> findById(Long id ){
        return communityDao.findById(id);
    };
}
