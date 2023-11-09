package com.example.Entity;

import jakarta.persistence.Embeddable;

@Embeddable
public class ImageProduct{
    private String name;

    private String type;

    private String imageToBase64;


    public ImageProduct() {
    }

    public ImageProduct(String name, String type,String imageToBase64) {
        this.name = name;
        this.type = type;
        this.imageToBase64 = imageToBase64;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImageToBase64() {
        return imageToBase64;
    }

    public void setImageToBase64(String imageToBase64) {
        this.imageToBase64 = imageToBase64;
    }
}
