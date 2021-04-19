/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 *
 * @author Tas
 */
@Entity
@Table(name = "posts")
public class Post implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "post_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_content", length = 282)
    private String postContent;
    
    @Column(name = "post_date")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dateCreated;
    
    @Column(name = "post_is_active")
    private int isActive;

    @JoinColumn(name = "user_name", referencedColumnName = "user_name")
    @ManyToOne
    private User user;
    
    @Column(name = "Category")
    private String category;

    public Post(String postContent, String category) {
        this.postContent = postContent;
        this.dateCreated = new Date();
        this.isActive=1;
        this.category = category;
    }

    public Post() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getPostContent() {
        return postContent;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}
