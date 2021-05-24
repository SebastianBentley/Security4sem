package facades;

import entities.Post;
import entities.Role;
import entities.User;
import errorhandling.API_Exception;
import utils.EMF_Creator;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import security.errorhandling.AuthenticationException;

//Uncomment the line below, to temporarily disable this test
public class PostFacadeTest {

    private static EntityManagerFactory emf;
    private static PostFacade facade;
    private User user;

    public PostFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = PostFacade.getPostFacade(emf);
    }

    @AfterAll
    public static void tearDownClass() {
//        Clean up database after test is done or use a persistence unit with drop-and-create to start up clean on every test
    }

    // Setup the DataBase in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the code below to use YOUR OWN entity class
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE from Role").executeUpdate();
            em.createQuery("DELETE from Post").executeUpdate();
            em.persist(new Role("user"));
            em.getTransaction().commit();
            em.getTransaction().begin();
            em.createQuery("DELETE from User").executeUpdate();
            em.persist(new User("Some txt", "More text"));
            user = new User("aaa", "bbb");
            Post post = new Post("Test", "gaming");
            user.addPost(post);
            em.persist(user);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @AfterEach
    public void tearDown() {
//        Remove any data after each test was run
    }

    
    @Test
    public void testUserPosts() throws AuthenticationException, IllegalAccessException {
        assertEquals(1, facade.getUserPosts("aaa").size());
    }
    
    
    @Test
    public void testAddPost() throws AuthenticationException, IllegalAccessException, API_Exception {
        facade.addPost("Added post", "aaa", "news");
        assertEquals(2, facade.getUserPosts("aaa").size());
    }
    

    //Test add post with non existing category
    @Test
    public void testAddPostInvalidCategory() throws AuthenticationException, API_Exception {

        Assertions.assertThrows(API_Exception.class, () -> {
            facade.addPost("Failme", "aaa", "fail");
        });

    }
    
    //Test add post with non existing category
    @Test
    public void testAddPostInvalidUsername() throws AuthenticationException {

        Assertions.assertThrows(AuthenticationException.class, () -> {
            facade.addPost("Failme", "fisk", "news");
        });

    }
    
    //Test add post with non existing category
    @Test
    public void testAddPostInvalidContent() throws AuthenticationException, IllegalAccessException {

        Assertions.assertThrows(IllegalAccessException.class, () -> {
            facade.addPost("<script>Alert</script>", "aaa", "news");
        });

    }
    
    
}

