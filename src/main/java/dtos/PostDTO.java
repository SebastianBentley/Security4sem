package dtos;

import entities.Post;
import java.util.Date;


public class PostDTO {
    private String username, post;
    private Date date;

    public PostDTO() {
    }

    public PostDTO(Post post) {
        this.username = post.getUser().getUserName();
        this.post = post.getPostContent();
        this.date = post.getDateCreated();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    
    
    
    
    
    
}
