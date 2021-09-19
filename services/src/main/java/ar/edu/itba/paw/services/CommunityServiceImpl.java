package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistance.CommunityDao;
import ar.edu.itba.paw.interfaces.services.CommunityService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommunityServiceImpl implements CommunityService {

    @Autowired
    private CommunityDao communityDao;

    @Autowired
    private UserService userService;

    @Override
    public List<Community> list(){ return communityDao.list();}

    @Override
    public Optional<Community> findById(Number id ){
        return communityDao.findById(id);
    };

    @Override
    public Optional<Community> create(String name, String description, User moderator){
        if(name == null || name.isEmpty() || description == null || description.isEmpty()){
            return Optional.empty();
        }
        //acá iría un chequeo de si el usuario esta logueado o no, pero pensamos atajar eso
        // desde el front y no dejar entrar a nadie al flujo de crear comunidad si no estan
        // logueados. Entonces puedo confiar que el moderator es un usuario bien.
        return Optional.ofNullable(communityDao.create(name, description, moderator));
    }
}
