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
    private Pattern usernamePattern = Pattern.compile("[A-Za-z0-9_]+");

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

    public User getVeryfiedUser(String username, String password) throws AuthenticationException, IllegalAccessException {
        EntityManager em = emf.createEntityManager();
        User user;
        boolean valid = (username != null) && username.length() < 64 && usernamePattern.matcher(username).matches();

        if (!valid) {
            throw new IllegalAccessException("Username has invalid symbols, or is over 64 characters long.");
        }
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
        if (!isValid(password)) {
            throw new IllegalAccessException("Password must have a minimum of 8 characters, maximum of 64 characters, and contain at least one digit, one special character, and one lowercase and uppercase character between a and z");
        }

        boolean valid = (username != null) && username.length() < 64 && usernamePattern.matcher(username).matches();

        if (!valid) {
            throw new IllegalAccessException("Username has invalid symbols, or is over 64 characters long.");
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

    public void changePassword(String username, String password, String newPassword1, String newPassword2) throws AuthenticationException, IllegalAccessException {
        EntityManager em = emf.createEntityManager();

        try {
            if (!isValid(newPassword2)) {
                throw new IllegalAccessException("Password must have a minimum of 8 characters, maximum of 64 characters, and contain at least one digit, one special character, and one lowercase and uppercase character between a and z");
            }
            if (!newPassword1.equals(newPassword2)) {
                throw new IllegalAccessException("Passwords does not match.");
            }

            User checkUser = getVeryfiedUser(username, password);
            if (!checkUser.getUserName().equals(username)) {
                throw new IllegalAccessException("User does not match logged in user.");
            }

            em.getTransaction().begin();
            User user = em.find(User.class, username);
            user.setUserPass(newPassword2);
            em.getTransaction().commit();

        } finally {
            em.close();
        }

    }

    //checks for lowercase, uppercase, special character, digit and a passwordlength between 8 and 64 characters
    private static final String PASSWORD_PATTERN
            = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,64}$";

    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    private static boolean isValid(final String password) {
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

}
