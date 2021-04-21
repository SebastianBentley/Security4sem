package facades;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dtos.PostDTO;
import dtos.UserDTO;
import entities.Post;
import entities.Role;
import entities.User;
import errorhandling.API_Exception;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import security.errorhandling.AuthenticationException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.TypedQuery;

public class PostFacade {

    private static EntityManagerFactory emf;
    private static PostFacade instance;
    private final ArrayList<String> CATEGORIES = new ArrayList(Arrays.asList("sport", "news", "social", "wealth", "gaming"));

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

    public void addPost(String postContent, String userName, String category) throws API_Exception, AuthenticationException {
        EntityManager em = emf.createEntityManager();
        if (postContent.length() < 0 || postContent.length() >= 281) {
            throw new API_Exception("Posts can only be between 0 and 281 characters");
        }
        if (!CATEGORIES.contains(category.toLowerCase())) {
            throw new API_Exception("Category does not exist");
        }
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, userName);
            if (user == null) {
                throw new AuthenticationException("User with the username provided does not exist");
            } else {
                Post post = new Post(postContent, category);
                user.addPost(post);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public ArrayList<PostDTO> getAllPosts() {
        EntityManager em = emf.createEntityManager();
        ArrayList<PostDTO> results = new ArrayList();
        try {
            TypedQuery<Post> query = em.createQuery("select p from Post p where p.isActive = 1 ", entities.Post.class);
            List<Post> posts = query.getResultList();
            for (Post post : posts) {
                results.add(new PostDTO(post));
            }
            return results;
        } finally {
            em.close();
        }
    }
    
    public ArrayList<PostDTO> getCategoryPosts(String category) {
        EntityManager em = emf.createEntityManager();
        ArrayList<PostDTO> results = new ArrayList();
        try {
            TypedQuery<Post> query = em.createQuery("select p from Post p where p.isActive = 1 AND p.category = :category ", entities.Post.class);
            query.setParameter("category", category);
            List<Post> posts = query.getResultList();
            for (Post post : posts) {
                results.add(new PostDTO(post));
            }
            return results;
        } finally {
            em.close();
        }
    }
    public ArrayList<PostDTO> getUserPosts(String name) {
        EntityManager em = emf.createEntityManager();
        ArrayList<PostDTO> results = new ArrayList();
        try {
            TypedQuery<Post> query = em.createQuery("select p from Post p where p.isActive = 1 AND p.user.userName = :name ", entities.Post.class);
            query.setParameter("name", name);
            List<Post> posts = query.getResultList();
            for (Post post : posts) {
                results.add(new PostDTO(post));
            }
            return results;
        } finally {
            em.close();
        }
    }
}
