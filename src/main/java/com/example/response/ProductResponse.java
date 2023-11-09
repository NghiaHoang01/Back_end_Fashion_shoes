package com.example.response;

import com.example.Entity.Product;

public class ProductResponse extends Response{
    private Product product;

    public ProductResponse() {
    }

    public ProductResponse(Product product) {
        this.product = product;
    }

    public ProductResponse(String message, Boolean success, Product product) {
        super(message, success);
        this.product = product;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
