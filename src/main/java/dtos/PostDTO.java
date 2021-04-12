package dtos;

import entities.Post;
import java.util.Date;

public class PostDTO {

    private String username, post;
    private Date date;
    private Long id;

    public PostDTO() {
    }

    public PostDTO(Post post) {
        this.username = post.getUser().getUserName();
        this.post = post.getPostContent();
        this.date = post.getDateCreated();
        this.id = post.getId();
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
