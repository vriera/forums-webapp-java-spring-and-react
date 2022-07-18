package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.persistance.CommunityDao;
import ar.edu.itba.paw.interfaces.services.ForumService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.AccessType;
import ar.edu.itba.paw.models.Community;
import ar.edu.itba.paw.models.Forum;
import ar.edu.itba.paw.models.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Transactional
@RunWith(MockitoJUnitRunner.class)
public class CommunityServiceImplTest {

    private static final long MOD_ID = 1;
    private static final String MOD_USERNAME = "moderator";
    private static final String MOD_EMAIL = "example@email.com";
    private static final String MOD_PASSWORD = "password";
    private static final User MOD = new User(MOD_ID, MOD_USERNAME, MOD_EMAIL, MOD_PASSWORD);

    private static final long COMMUNITY_ID = 1;
    private static final String NAME = "Mock Community name";
    private static final String DESCRIPTION = "Mock Community description";
    private static final Community COMMUNITY = new Community(COMMUNITY_ID, NAME, DESCRIPTION, MOD);

    private static final long FORUM_ID = 1;
    private static final String FORUM_NAME = "Mock forum";
    private static final Forum FORUM = new Forum(FORUM_ID, FORUM_NAME, COMMUNITY);

    private static final long USER_ID = 2;
    private static final String USER_USERNAME = "user";
    private static final String USER_EMAIL = "example+1@email.com";
    private static final String USER_PASSWORD = "password";
    private static final User USER = new User(USER_ID, USER_USERNAME, USER_EMAIL, USER_PASSWORD);

    @InjectMocks
    private CommunityServiceImpl communityService = new CommunityServiceImpl();

    @Mock
    private CommunityDao communityDao;

    @Mock
    private UserService userService;

    @Mock
    private ForumService forumService;

    @Test
    public void testCreateUserExists(){
        Mockito.when(communityDao.create(NAME, DESCRIPTION, MOD))
                .thenReturn(COMMUNITY);
        Mockito.when(forumService.create(COMMUNITY)).thenReturn(Optional.of(FORUM));
        Optional<Community> c = communityService.create(NAME, DESCRIPTION, MOD);

        Assert.assertNotNull(c);
        assertTrue(c.isPresent());
        Assert.assertEquals(NAME, c.get().getName());
        Assert.assertEquals(DESCRIPTION, c.get().getDescription());
        Assert.assertEquals(MOD, c.get().getModerator());
    }

    @Test
    public void testCreateNoName(){
        //Mockito.when(communityDao.create(NAME, DESCRIPTION, OWNER)).thenReturn(new Community(1, NAME, DESCRIPTION, OWNER));
        Optional<Community> c = communityService.create("", DESCRIPTION, MOD);
        Assert.assertNotNull(c);
        //este test deberia salir bien si el optional no esta presente (no me pasan nombre no creo comunidad)
        Assert.assertFalse(c.isPresent());
    }

    @Test
    public void testCreateNullName(){
        //Mockito.when(communityDao.create(NAME, DESCRIPTION, OWNER)).thenReturn(new Community(1, NAME, DESCRIPTION, OWNER));
        Optional<Community> c = communityService.create(null, DESCRIPTION, MOD);
        Assert.assertNotNull(c);
        //este test deberia salir bien si el optional no esta presente (no me pasan nombre no creo comunidad)
        Assert.assertFalse(c.isPresent());
    }

    @Test
    public void testNewRequest(){
        Mockito.when(userService.findById(USER_ID)).thenReturn(Optional.of(USER));
        Mockito.when(communityDao.findById(COMMUNITY_ID)).thenReturn(Optional.of(COMMUNITY));
        Mockito.when(communityDao.getAccess(USER_ID, COMMUNITY_ID)).thenReturn(Optional.empty());

        boolean success = communityService.requestAccess(USER_ID, COMMUNITY_ID);

        assertTrue(success);
    }

    @Test
    public void testModBan(){
        Mockito.when(userService.findById(MOD_ID)).thenReturn(Optional.of(MOD));
        Mockito.when(communityDao.findById(COMMUNITY_ID)).thenReturn(Optional.of(COMMUNITY));

        boolean success = communityService.requestAccess(MOD_ID, COMMUNITY_ID);

        assertFalse(success);
    }

    @Test
    public void testCanAccessUserNullCommunityPrivate(){
        Mockito.when(userService.findById(USER_ID)).thenReturn(Optional.of(USER));
        Mockito.when(communityService.findById(COMMUNITY_ID)).thenReturn(Optional.of(COMMUNITY));

        boolean canAccess = communityService.canAccess(null, COMMUNITY);

        assertFalse(canAccess);
    }

    @Test
    public void testCanAccessUserIsMod(){
        Mockito.when(userService.findById(USER_ID)).thenReturn(Optional.of(USER));
        Mockito.when(communityService.findById(COMMUNITY_ID)).thenReturn(Optional.of(COMMUNITY));

        boolean canAccess = communityService.canAccess(MOD, COMMUNITY);

        assertTrue(canAccess);
    }

    @Test
    public void testCanAccessDenied(){
        Mockito.when(communityService.getAccess(USER_ID, COMMUNITY_ID)).thenReturn(Optional.of(AccessType.BANNED));
        Mockito.when(userService.findById(USER_ID)).thenReturn(Optional.of(USER));
        Mockito.when(communityService.findById(COMMUNITY_ID)).thenReturn(Optional.of(COMMUNITY));

        boolean canAccess = communityService.canAccess(USER, COMMUNITY);

        assertFalse(canAccess);
    }

    @Test
    public void testCanAccessGranted(){
        Mockito.when(communityService.getAccess(USER_ID, COMMUNITY_ID)).thenReturn(Optional.of(AccessType.ADMITTED));
        Mockito.when(userService.findById(USER_ID)).thenReturn(Optional.of(USER));
        Mockito.when(communityService.findById(COMMUNITY_ID)).thenReturn(Optional.of(COMMUNITY));

        boolean canAccess = communityService.canAccess(USER, COMMUNITY);

        assertTrue(canAccess);
    }

    @Test
    public void unauthorizedAdmit(){
        Mockito.when(communityService.getAccess(USER_ID, COMMUNITY_ID)).thenReturn(Optional.of(AccessType.REQUESTED));
        Mockito.when(userService.findById(USER_ID)).thenReturn(Optional.of(USER));
        Mockito.when(communityService.findById(COMMUNITY_ID)).thenReturn(Optional.of(COMMUNITY));
        boolean success = communityService.admitAccess(USER_ID, COMMUNITY_ID, USER_ID);

        assertFalse(success);
    }



}
