package com.example.response;

import com.example.Entity.Cart;

public class CartResponse extends Response{
    private Cart cart;

    public CartResponse() {
    }

    public CartResponse(Cart cart) {
        this.cart = cart;
    }

    public CartResponse(String message, Boolean success, Cart cart) {
        super(message, success);
        this.cart = cart;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }
}
