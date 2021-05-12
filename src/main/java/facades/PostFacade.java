package facades;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dtos.CommentDTO;
import dtos.PostDTO;
import dtos.UserDTO;
import entities.Comment;
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
    private Pattern postPattern = Pattern.compile("[A-Za-z0-9 ]+");

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

    public void addPost(String postContent, String userName, String category) throws API_Exception, AuthenticationException, IllegalAccessException {
        EntityManager em = emf.createEntityManager();
        if (postContent.length() < 0 || postContent.length() >= 281) {
            throw new API_Exception("Posts can only be between 0 and 281 characters");
        }
        if (!CATEGORIES.contains(category.toLowerCase())) {
            throw new API_Exception("Category does not exist");
        }
        
        boolean valid = ((postContent != null) && postPattern.matcher(postContent).matches());

        if (!valid) {
            throw new IllegalAccessException("Username has invalid symbols, or is over 64 characters long.");
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
    
    public void addComment(String userName, String comContent, Long postID) throws API_Exception, AuthenticationException {
        EntityManager em = emf.createEntityManager();
        if (comContent.length() < 0 || comContent.length() >= 281) {
            throw new API_Exception("Posts can only be between 0 and 281 characters");
        }
        
        boolean valid = ((comContent != null) && postPattern.matcher(comContent).matches());

        if (!valid) {
            throw new API_Exception("Username has invalid symbols, or is over 64 characters long, or comment has invalid symbols.");
        }
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, userName);
            Post post = em.find(Post.class, postID);
            if (user == null) {
                throw new AuthenticationException("User with the username provided does not exist");
            } else if(post == null) {
                throw new API_Exception("Post does not exist");
            } else {
                Comment comment = new Comment(comContent);
                comment.setUser(user);
                post.addComment(comment);
                
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
    
    public ArrayList<CommentDTO> getAllComments(Long postID) {
        EntityManager em = emf.createEntityManager();
        ArrayList<CommentDTO> results = new ArrayList();
        try {
            TypedQuery<Comment> query = em.createQuery("select p from Comment p where p.post.id = :postID ", entities.Comment.class);
            query.setParameter("postID", postID);
            List<Comment> comments = query.getResultList();
            for (Comment comment : comments) {
                results.add(new CommentDTO(comment));
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
