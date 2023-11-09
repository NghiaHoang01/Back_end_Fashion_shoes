package com.example.service.implement;

import com.example.Entity.Cart;
import com.example.Entity.Product;
import com.example.Entity.Size;
import com.example.Entity.User;
import com.example.config.JwtProvider;
import com.example.exception.CustomException;
import com.example.repository.CartRepository;
import com.example.repository.ProductRepository;
import com.example.request.CartRequest;
import com.example.service.CartService;
import com.example.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public Cart addToCart(CartRequest cartRequest) throws CustomException {

        String token = jwtProvider.getTokenFromCookie(request);
        User user = userService.findUserProfileByJwt(token);

        List<Cart> carts = cartRepository.findByUserId(user.getId());

        if (carts.size() <= 50) {
            Optional<Product> product = productRepository.findById(cartRequest.getProductId());

            if (product.isPresent()) {

                List<Size> checkSize = product.get().getSizes().stream().filter(s -> Objects.equals(s.getName(), cartRequest.getSize())).toList();

                if (checkSize.size() != 0) {
                    List<Cart> checkCartItemExist = cartRepository.findByUserIdAndProductIdAndSize(user.getId(), cartRequest.getProductId(), cartRequest.getSize());

                    if (checkCartItemExist.size() != 0) {
                        Cart oldCart = checkCartItemExist.get(0);
                        if (oldCart.getQuantity() < 10) {
                            oldCart.setQuantity(oldCart.getQuantity() + 1);
                            oldCart.setTotalPrice(oldCart.getQuantity()*oldCart.getProduct().getDiscountedPrice());
                            oldCart.setUpdateBy(user.getId());
                        }
                        return cartRepository.save(oldCart);
                    } else {
                        Cart cart = new Cart();
                        cart.setUser(user);
                        cart.setProduct(product.get());
                        cart.setSize(cartRequest.getSize());
                        cart.setQuantity(cartRequest.getQuantity()); // quantity mặc định ban đầu là = 1
                        cart.setTotalPrice(product.get().getDiscountedPrice()*cartRequest.getQuantity());
                        cart.setCreatedBy(user.getId());

                        return cartRepository.save(cart);
                    }
                } else {
                    throw new CustomException("Invalid size name !!!");
                }
            } else {
                throw new CustomException("Product not found with id: " + cartRequest.getProductId());
            }
        } else {
            return null;
        }
    }

    @Override
    @Transactional
    public Cart updateCartItem(CartRequest cartRequest, Long id) throws CustomException {
        Optional<Cart> oldCartItem = cartRepository.findById(id);

        if (oldCartItem.isPresent()) {
            String token = jwtProvider.getTokenFromCookie(request);
            User user = userService.findUserProfileByJwt(token);

            if (user.getId().equals(oldCartItem.get().getUser().getId())) {
                if (cartRequest.getQuantity() > 0 && cartRequest.getQuantity() <= 10) {
                    oldCartItem.get().setSize(cartRequest.getSize()); // vẫn giữ nguyên size ban đầu
                    oldCartItem.get().setQuantity(cartRequest.getQuantity());
                    oldCartItem.get().setTotalPrice(oldCartItem.get().getProduct().getDiscountedPrice()*cartRequest.getQuantity());
                    oldCartItem.get().setUpdateBy(user.getId());

                    return cartRepository.save(oldCartItem.get());
                } else {
                    throw new CustomException("Invalid quantity !!!");
                }
            } else {
                throw new CustomException("You do not have permission to update !!!");
            }
        } else {
            throw new CustomException("Cart item not found with id: " + id);
        }
    }

    @Override
    @Transactional
    public String deleteCartItem(Long id) throws CustomException {
        Optional<Cart> cart = cartRepository.findById(id);

        if (cart.isPresent()) {
            String token = jwtProvider.getTokenFromCookie(request);
            User user = userService.findUserProfileByJwt(token);

            if (user.getId().equals(cart.get().getUser().getId())) {
                cartRepository.delete(cart.get());
                return "Delete cart item success !!!";
            } else {
                throw new CustomException("You do not have permission to delete !!!");
            }
        } else {
            throw new CustomException("Cart item not found with id: " + id);
        }
    }

    @Override
    public List<Cart> getCartDetails(int pageIndex, int pageSize) throws CustomException {
        String token = jwtProvider.getTokenFromCookie(request);
        User user = userService.findUserProfileByJwt(token);

        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);

        List<Cart> carts = cartRepository.findByUserIdOrderByIdDesc(user.getId());

        int startIndex = (int) pageable.getOffset();
        int endIndex = Math.min(startIndex + pageable.getPageSize(), carts.size());

        return carts.subList(startIndex, endIndex);
    }
}
