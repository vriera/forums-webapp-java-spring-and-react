import ar.edu.itba.paw.interfaces.persistance.CommunityDao;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.services.CommunityServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class CommunityServiceImplTest {
    private static final String NAME = "Mock Community name";
    private static final String DESCRIPTION = "Mock Community description";
    private static final String EMAIL = "example@email.com";
    private static final String USERNAME = "user";
    private static final String PASSWORD = "password";
    private static final User OWNER = new User(1, USERNAME, EMAIL, PASSWORD);


    @InjectMocks
    CommunityServiceImpl communityService = new CommunityServiceImpl();

    @Mock
    private CommunityDao mockDao;

    @Test
    public void testCreateUserExists(){
        Mockito.when(mockDao.create(NAME, DESCRIPTION, OWNER))
                .thenReturn(new Community(1, NAME, DESCRIPTION, OWNER));

        Optional<Community> c = communityService.create(NAME, DESCRIPTION, OWNER);

        Assert.assertNotNull(c);
        Assert.assertTrue(c.isPresent());
        Assert.assertEquals(NAME, c.get().getName());
        Assert.assertEquals(DESCRIPTION, c.get().getDescription());
        Assert.assertEquals(OWNER, c.get().getModerator());
    }

    @Test
    public void testCreateNoName(){
        //Mockito.when(mockDao.create(NAME, DESCRIPTION, OWNER)).thenReturn(new Community(1, NAME, DESCRIPTION, OWNER));
        Optional<Community> c = communityService.create("", DESCRIPTION, OWNER);
        Assert.assertNotNull(c);
        //este test deberia salir bien si el optional no esta presente (no me pasan nombre no creo comunidad)
        Assert.assertTrue(!c.isPresent());
    }

    @Test
    public void testCreateNullName(){
        //Mockito.when(mockDao.create(NAME, DESCRIPTION, OWNER)).thenReturn(new Community(1, NAME, DESCRIPTION, OWNER));
        Optional<Community> c = communityService.create(null, DESCRIPTION, OWNER);
        Assert.assertNotNull(c);
        //este test deberia salir bien si el optional no esta presente (no me pasan nombre no creo comunidad)
        Assert.assertTrue(!c.isPresent());
    }



}
