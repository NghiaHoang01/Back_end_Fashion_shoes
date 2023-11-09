package com.example.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "comment")
public class Comment extends BaseEntity{
    @Column(name = "comment")
    private String comment;

//    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product productOfComment;

//    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userOfComment;

    @ElementCollection
    private Set<ImageProduct> imageComments;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Product getProductOfComment() {
        return productOfComment;
    }

    public void setProductOfComment(Product productOfComment) {
        this.productOfComment = productOfComment;
    }

    public User getUserOfComment() {
        return userOfComment;
    }

    public void setUserOfComment(User userOfComment) {
        this.userOfComment = userOfComment;
    }

    public Set<ImageProduct> getImageComments() {
        return imageComments;
    }

    public void setImageComments(Set<ImageProduct> imageComments) {
        this.imageComments = imageComments;
    }

    public Comment() {
    }

    public Comment(String comment, Product productOfComment, User userOfComment, Set<ImageProduct> imageComments) {
        this.comment = comment;
        this.productOfComment = productOfComment;
        this.userOfComment = userOfComment;
        this.imageComments = imageComments;
    }
}
