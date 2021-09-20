import ar.edu.itba.paw.interfaces.persistance.CommunityDao;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.services.CommunityServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CommunityServiceImplTest {
    private static final String NAME = "Mock Community name";
    private static final String DESCRIPTION = "Mock Community description";
    private static final String EMAIL = "example@email.com";
    private static final String USERNAME = "user";
    private static final User OWNER = new User(1, USERNAME, EMAIL);


    @InjectMocks
    CommunityServiceImpl communityService = new CommunityServiceImpl();

    @Mock
    private CommunityDao mockDao;

    @Test
    public void testCreateUserExists(){
        Mockito.when(mockDao.create(NAME, DESCRIPTION, OWNER))
                .thenReturn(new Community(1, NAME, DESCRIPTION));
    }



}
