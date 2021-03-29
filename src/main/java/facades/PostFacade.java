package facades;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dtos.UserDTO;
import entities.Post;
import entities.Role;
import entities.User;
import errorhandling.API_Exception;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import security.errorhandling.AuthenticationException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PostFacade {

    private static EntityManagerFactory emf;
    private static PostFacade instance;

    private PostFacade() {
    }

    /**
     *
     * @param _emf
     * @return the instance of this facade.
     */
    public static PostFacade getPostFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new PostFacade();
        }
        return instance;
    }

    public void addPost(String postContent, String userName) throws API_Exception, AuthenticationException {
        EntityManager em = emf.createEntityManager();
        if (postContent.length() >= 281) {
            throw new API_Exception("Posts can only be a maximum of 281 characters");
        }
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, userName);
            if (user == null) {
                throw new AuthenticationException("User with the username provided does not exist");
            } else {
                Post post = new Post(postContent);
                user.addPost(post);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

}
