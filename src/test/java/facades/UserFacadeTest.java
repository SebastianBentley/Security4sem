package facades;

import entities.Role;
import entities.User;
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
public class UserFacadeTest {

    private static EntityManagerFactory emf;
    private static UserFacade facade;
    private User user;

    public UserFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = UserFacade.getUserFacade(emf);
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
    public void testUserName() throws AuthenticationException, IllegalAccessException {
        assertEquals("aaa", facade.getVeryfiedUser("aaa", "bbb").getUserName(), "Expects same name");
    }

    //Test user can register with correct password format
    @Test
    public void testRegisterUser() throws AuthenticationException, IllegalAccessException {
        facade.registerUser("fiske", "JuiceIsLoose123/");
        assertEquals("fiske", facade.getVeryfiedUser("fiske", "JuiceIsLoose123/").getUserName(), "Expects user exist after register");
    }

    //Test user can register with incorrect password format
    @Test
    public void testRegisterUserInvalidInputshort() throws AuthenticationException, IllegalAccessException {

        Assertions.assertThrows(IllegalAccessException.class, () -> {
            facade.registerUser("fiske", "juice");
        });

    }

    //Test user can register with incorrect password format
    @Test
    public void testRegisterUserInvalidInputLong() throws AuthenticationException, IllegalAccessException {

        Assertions.assertThrows(IllegalAccessException.class, () -> {
            facade.registerUser("fiske", "juicejuicejuicejuicejuicejuicejuicejuicejuicejuicejuicejuicejuicejuicejuicejuice");
        });

    }

    //Test user can register with incorrect password format
    @Test
    public void testRegisterUserInvalidInputNoSpecial() throws AuthenticationException, IllegalAccessException {

        Assertions.assertThrows(IllegalAccessException.class, () -> {
            facade.registerUser("fiske", "juiceLoose123");
        });

    }

    //Test user can register with incorrect password format
    @Test
    public void testRegisterUserInvalidInputNoNumbers() throws AuthenticationException, IllegalAccessException {

        Assertions.assertThrows(IllegalAccessException.class, () -> {
            facade.registerUser("fiske", "juiceLoose/");
        });

    }

    //Test change password valid input
    @Test
    public void testChangePassword() throws AuthenticationException, IllegalAccessException {
        EntityManager em = emf.createEntityManager();
        User finduser;
        String userName = "aaa";
        String newPassword = "JuiceIsLoose123/";
        facade.changePassword(userName, "bbb", newPassword, newPassword);
        try {
            em.getTransaction().begin();
            finduser = em.find(User.class, userName);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        assertTrue(finduser.verifyPassword(newPassword));

    }

    
    //Test change password invalid input
    @Test
    public void testChangePasswordInvalidInputShort() throws AuthenticationException, IllegalAccessException {
        String newPassword = "juice";
        Assertions.assertThrows(IllegalAccessException.class, () -> {
            facade.changePassword("aaa", "bbb", newPassword, newPassword);
        });

    }
    
    //Test change password invalid input
    @Test
    public void testChangePasswordInvalidInputLong() throws AuthenticationException, IllegalAccessException {
     
        String newPassword = "juicejuicejuicejuicejuicejuicejuicejuicejuicejuicejuicejuicejuicejuicejuicejuice";
        Assertions.assertThrows(IllegalAccessException.class, () -> {
            facade.changePassword("aaa", "bbb", newPassword, newPassword);
        });

    }
    
    
    //Test change password invalid input
    @Test
    public void testChangePasswordInvalidInputNoNumbers() throws AuthenticationException, IllegalAccessException {
     
        String newPassword = "juiceIsLoose/";
        Assertions.assertThrows(IllegalAccessException.class, () -> {
            facade.changePassword("aaa", "bbb", newPassword, newPassword);
        });

    }
    
    
     //Test change password invalid input
    @Test
    public void testChangePasswordInvalidInputNoSpecial() throws AuthenticationException, IllegalAccessException {
     
        String newPassword = "juiceIsLoose123";
        Assertions.assertThrows(IllegalAccessException.class, () -> {
            facade.changePassword("aaa", "bbb", newPassword, newPassword);
        });

    }

}
