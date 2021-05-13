package dtos;

import entities.Comment;
import java.util.Date;

public class CommentDTO {

    private String username, post;
    private Date date;
    private Long id;

    public CommentDTO() {
    }

    public CommentDTO(Comment comment) {
        this.username = comment.getUser().getUserName();
        this.post = comment.getCommentContent();
        this.date = comment.getDateCreated();
        this.id = comment.getId();
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
