package com.example.service;

import com.example.Entity.Cart;
import com.example.exception.CustomException;
import com.example.request.CartRequest;

import java.util.List;

public interface CartService {
    Cart addToCart(CartRequest cartRequest) throws CustomException;

    Cart updateCartItem(CartRequest cartRequest, Long id) throws CustomException;
    String deleteCartItem(Long id) throws CustomException;

    List<Cart> getCartDetails(int pageIndex, int pageSize) throws CustomException;
}
