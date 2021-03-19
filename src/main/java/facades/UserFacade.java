package facades;

import dtos.UserDTO;
import entities.Role;
import entities.User;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import security.errorhandling.AuthenticationException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class UserFacade {

    private static EntityManagerFactory emf;
    private static UserFacade instance;

    private UserFacade() {
    }

    /**
     *
     * @param _emf
     * @return the instance of this facade.
     */
    public static UserFacade getUserFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new UserFacade();
        }
        return instance;
    }

    public User getVeryfiedUser(String username, String password) throws AuthenticationException {
        EntityManager em = emf.createEntityManager();
        User user;
        try {
            user = em.find(User.class, username);
            if (user == null || !user.verifyPassword(password)) {
                throw new AuthenticationException("Invalid user name or password");
            }
        } finally {
            em.close();
        }
        return user;
    }

    public UserDTO registerUser(String username, String password) throws IllegalAccessException {
        EntityManager em = emf.createEntityManager();
//        if (username.equals("") || password.equals("") || password.length() <= 8 || password.length() > 64) {
//        }
        if (!isValid(password)) {
            throw new IllegalAccessException("Password must have a minimum of 8 characters, maximum of 64 characters, and contain at least one digit, one special character, and one lowercase and uppercase character between a and z");
        }

        User user = new User(username, password);
        Role userRole = new Role("user");
        user.addRole(userRole);

        try {
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return new UserDTO(user);

    }
    
    //checks for lowercase, uppercase, special character, digit and a passwordlength between 8 and 64 characters
    private static final String PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,64}$";

    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    private static boolean isValid(final String password) {
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

}
