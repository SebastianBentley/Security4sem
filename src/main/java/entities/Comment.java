
package entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;


@Entity
@Table(name = "comments")
public class Comment implements Serializable {
    
    
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "comment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "comment_content", length = 282)
    private String commentContent;
    
    @Column(name = "comment_date")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dateCreated;
    
    @OneToOne(cascade = CascadeType.PERSIST)
    private User user;
    
    @JoinColumn(name = "post_id", referencedColumnName = "post_id")
    @ManyToOne
    private Post post;

    public Comment(String commentContent) {
        this.commentContent = commentContent;
        this.dateCreated = new Date();
    }

    public Comment() {
        
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }
    
    
    public Date getDateCreated() {
    return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        if(this.user != null) {
            user.setUser_comment(this);
        }
    }
    
}
