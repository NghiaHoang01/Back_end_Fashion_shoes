package com.example.request;

import com.example.Entity.Size;

import java.util.Set;

public class ProductRequest {
    private String name;
    private String title;
    private String description;
    private String brand;
    private String parentCategory;
    private String childCategory;
    private int discountedPercent;
    private double price;
    private String colors;
    private Set<Size> sizes;

    public ProductRequest() {
    }

    public ProductRequest(String name, String title, String description, String brand, String parentCategory, String childCategory, int discountedPercent, double price, String colors, Set<Size> sizes) {
        this.name = name;
        this.title = title;
        this.description = description;
        this.brand = brand;
        this.parentCategory = parentCategory;
        this.childCategory = childCategory;
        this.discountedPercent = discountedPercent;
        this.price = price;
        this.colors = colors;
        this.sizes = sizes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(String parentCategory) {
        this.parentCategory = parentCategory;
    }

    public String getChildCategory() {
        return childCategory;
    }

    public void setChildCategory(String childCategory) {
        this.childCategory = childCategory;
    }

    public int getDiscountedPercent() {
        return discountedPercent;
    }

    public void setDiscountedPercent(int discountedPercent) {
        this.discountedPercent = discountedPercent;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getColors() {
        return colors;
    }

    public void setColors(String colors) {
        this.colors = colors;
    }

    public Set<Size> getSizes() {
        return sizes;
    }

    public void setSizes(Set<Size> sizes) {
        this.sizes = sizes;
    }
}
