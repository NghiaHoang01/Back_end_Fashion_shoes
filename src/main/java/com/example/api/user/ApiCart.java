package com.example.api.user;

import com.example.Entity.Cart;
import com.example.exception.CustomException;
import com.example.request.CartRequest;
import com.example.response.CartResponse;
import com.example.response.Response;
import com.example.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("cartOfUser")
@RequestMapping("/api/user")
public class ApiCart {
    @Autowired
    private CartService cartService;

    @PostMapping("/cart")
    public ResponseEntity<?> addCartItem(@RequestBody CartRequest cartRequest) throws CustomException {

        Cart cart = cartService.addToCart(cartRequest);

        CartResponse cartResponse = new CartResponse();
        cartResponse.setCart(cart);
        cartResponse.setMessage("Add cart item success !!!");
        cartResponse.setSuccess(true);

        return new ResponseEntity<>(cartResponse, HttpStatus.OK);
    }

    @PutMapping("/cart")
    public ResponseEntity<?> updateCartItem(@RequestBody CartRequest cartRequest,
                                            @RequestParam("id") Long id) throws CustomException {
        Cart cart = cartService.updateCartItem(cartRequest, id);

        CartResponse cartResponse = new CartResponse();
        cartResponse.setCart(cart);
        cartResponse.setMessage("Update cart item success !!!");
        cartResponse.setSuccess(true);

        return new ResponseEntity<>(cartResponse, HttpStatus.OK);
    }

    @DeleteMapping("/cart")
    public ResponseEntity<?> deleteCartItem(@RequestParam("id") Long id) throws CustomException {
        String message = cartService.deleteCartItem(id);

        Response response = new Response();
        response.setMessage(message);
        response.setSuccess(true);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/cart/detail")
    public ResponseEntity<?> getCartDetailOfUser(@RequestParam("pageIndex") int pageIndex,
                                                 @RequestParam("pageSize") int pageSize) throws CustomException {
        return new ResponseEntity<>(cartService.getCartDetails(pageIndex, pageSize), HttpStatus.OK);
    }
}
