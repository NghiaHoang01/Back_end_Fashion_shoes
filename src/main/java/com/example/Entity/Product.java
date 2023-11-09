package com.example.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "product")
public class Product extends BaseEntity {
    @Column(name = "name", columnDefinition = "TEXT")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "title", columnDefinition = "TEXT")
    private String title;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "price")
    private double price;

//    @Column(name = "image_url")
//    private String imageUrl;

    @ElementCollection
    @Column(name = "main_image")
    private Set<ImageProduct> mainImage;

    @Column(name = "discounted_percent")
    private int discountedPercent;

    @Column(name = "discounted_price")
    private double discountedPrice;

    @Column(name = "color")
    private String color;

    @JsonIgnore
    @OneToMany(mappedBy = "productOfComment")
    private Set<Comment> comments = new HashSet<>();

    //@JsonIgnore
    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brandProduct;

    //@JsonIgnore
    @ManyToOne
    @JoinColumn(name = "parentCategory_id")
    private ParentCategory parentCategoryOfProduct;

    //@JsonIgnore
    @ManyToOne
    @JoinColumn(name = "childCategory_id")
    private ChildCategory childCategoryOfProduct;

    @ElementCollection
    // dùng trong quan hệ One To Many, dùng khi bảng many chỉ có ý nghĩa khi gán với bảng phía one (hoặc không làm khóa ngoại của bảng khác)
    private Set<ImageProduct> imageProducts;

    @ElementCollection
    private Set<Size> sizes = new HashSet<>();

    //getter-setter

    public Set<ImageProduct> getImageProducts() {
        return imageProducts;
    }

    public void setImageProducts(Set<ImageProduct> imageProducts) {
        this.imageProducts = imageProducts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getDiscountedPercent() {
        return discountedPercent;
    }

    public void setDiscountedPercent(int discountedPercent) {
        this.discountedPercent = discountedPercent;
    }

    public double getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(double discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public Brand getBrandProduct() {
        return brandProduct;
    }

    public void setBrandProduct(Brand brandProduct) {
        this.brandProduct = brandProduct;
    }

    public ParentCategory getParentCategoryOfProduct() {
        return parentCategoryOfProduct;
    }

    public void setParentCategoryOfProduct(ParentCategory parentCategoryOfProduct) {
        this.parentCategoryOfProduct = parentCategoryOfProduct;
    }

    public ChildCategory getChildCategoryOfProduct() {
        return childCategoryOfProduct;
    }

    public void setChildCategoryOfProduct(ChildCategory childCategoryOfProduct) {
        this.childCategoryOfProduct = childCategoryOfProduct;
    }

    public Set<Size> getSizes() {
        return sizes;
    }

    public void setSizes(Set<Size> sizes) {
        this.sizes = sizes;
    }

    public Set<ImageProduct> getMainImage() {
        return mainImage;
    }

    public void setMainImage(Set<ImageProduct> mainImage) {
        this.mainImage = mainImage;
    }

    public Product() {
    }

    public Product(String name, String description, String title, int quantity, double price, Set<ImageProduct> mainImage, int discountedPercent, double discountedPrice, String color, Set<Comment> comments, Brand brandProduct,
                   ParentCategory parentCategoryOfProduct, ChildCategory childCategoryOfProduct, Set<ImageProduct> imageProducts, Set<Size> sizes) {
        this.name = name;
        this.description = description;
        this.title = title;
        this.quantity = quantity;
        this.price = price;
        this.mainImage = mainImage;
        this.discountedPercent = discountedPercent;
        this.discountedPrice = discountedPrice;
        this.color = color;
        this.comments = comments;
        this.brandProduct = brandProduct;
        this.parentCategoryOfProduct = parentCategoryOfProduct;
        this.childCategoryOfProduct = childCategoryOfProduct;
        this.imageProducts = imageProducts;
        this.sizes = sizes;
    }
}
