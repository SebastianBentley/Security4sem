package facades;


import entities.Post;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;


public class AdminFacade {

    private static EntityManagerFactory emf;
    private static AdminFacade instance;

    public AdminFacade() {
    }

    /**
     *
     * @param _emf
     * @return the instance of this facade.
     */
    public static AdminFacade getAdminFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new AdminFacade();
        }
        return instance;
    }

    public void deletePost(long postId) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Post post = em.find(Post.class, postId);
            post.setIsActive(0);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
    
}
