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

public class AdminFacadeTest {

    private static EntityManagerFactory emf;
    private static AdminFacade facade;
    private static PostFacade postfacade;
    private User userAdmin;
    private User userUser;

    public AdminFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = AdminFacade.getAdminFacade(emf);
        postfacade = PostFacade.getPostFacade(emf);
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
            Role adminRole = new Role("admin");
            Role userRole = new Role("user");
            em.persist(userRole);
            em.persist(adminRole);
            em.getTransaction().commit();
            em.getTransaction().begin();
            em.createQuery("DELETE from User").executeUpdate();
            userUser = new User("aaa", "userpassword");
            userUser.addRole(userRole);
            userAdmin = new User("admin", "adminpassword");
            userAdmin.addRole(adminRole);
            em.persist(userAdmin);
            Post post = new Post("Test", "gaming");
            userUser.addPost(post);
            em.persist(userUser);
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
    public void deleteUser() {
        int postSize = postfacade.getUserPosts("aaa").size();
        facade.deletePost(1);
        int newPostSize = postfacade.getUserPosts("aaa").size();
        assertEquals(postSize - 1, newPostSize);
    }

}
