package com.example.service.implement;

import com.example.Entity.*;
import com.example.config.JwtProvider;
import com.example.constant.OrderConstant;
import com.example.exception.CustomException;
import com.example.repository.CartRepository;
import com.example.repository.OrderLineRepository;
import com.example.repository.OrderRepository;
import com.example.repository.ProductRepository;
import com.example.request.OrderProductQuantityRequest;
import com.example.request.OrderRequest;
import com.example.service.OrderService;
import com.example.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderLineRepository orderLineRepository;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private UserService userService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartRepository cartRepository;

    @Override
    @Transactional
    public void placeOrder(OrderRequest orderRequest, Boolean isSingleProductBuyNow) throws CustomException {
        List<OrderProductQuantityRequest> orderProductQuantityRequests = orderRequest.getProductQuantities();

        String token = jwtProvider.getTokenFromCookie(request);
        User user = userService.findUserProfileByJwt(token);

        double totalPrice = 0;

        // nếu số lượng sản phẩm order nhiều hơn số lượng sản phẩm có trong kho thì nhân viên phải liên hệ khách hàng để thỏa thuận lại về đơn hàng

        // create order
        Order order = new Order();

        order.setAddress(orderRequest.getAddressOfUser());
        order.setDistrict(orderRequest.getDistrict());
        order.setProvince(orderRequest.getProvince());
        order.setWard(orderRequest.getWard());
        order.setFullName(orderRequest.getFullNameOfUser());
        order.setUser(user);
        order.setAlternatePhoneNumber(orderRequest.getAlternatePhoneNumber());
        order.setPhoneNumber(orderRequest.getPhoneNumber());
        order.setStatus(OrderConstant.ORDER_PENDING);
        if (orderRequest.getProvince().equalsIgnoreCase("ho chi minh")) {
            order.setTransportFee(0);
        } else {
            order.setTransportFee(30000L);
        }
        order.setTransactionId(orderRequest.getTransactionId());
        order.setCreatedBy(user.getId());

        for (OrderProductQuantityRequest p : orderProductQuantityRequests) {
            Product product = productRepository.findById(p.getProductId()).get();
            totalPrice += p.getQuantity() * product.getDiscountedPrice();
        }

        order.setTotalPrice(totalPrice);

        orderRepository.save(order);

        // create order line
        Order orderLasted = orderRepository.findTop1ByOrderByIdDesc();

        for (OrderProductQuantityRequest productOrder : orderProductQuantityRequests) {
            Optional<Product> product = productRepository.findById(productOrder.getProductId());

            if (product.isPresent()) {
                OrderLine orderLine = new OrderLine();

                orderLine.setOrder(orderLasted);

                orderLine.setProduct(product.get());
                orderLine.setQuantity(productOrder.getQuantity());
                orderLine.setSize(productOrder.getSize());
                orderLine.setCreatedBy(user.getId());
                orderLine.setTotalPrice(productOrder.getQuantity() * product.get().getDiscountedPrice());

                orderLineRepository.save(orderLine);
            } else {
                throw new CustomException("Product not found !!!");
            }
        }

        //delete products that have been buy in the shopping cart
        if (!isSingleProductBuyNow) {
            List<Cart> carts = cartRepository.findByUserId(user.getId());

            carts.forEach(c -> {
                orderProductQuantityRequests.forEach(p -> {
                    if (c.getSize() == p.getSize()
                            && Objects.equals(c.getProduct().getId(), p.getProductId())) {
                        cartRepository.deleteById(c.getId());
                    }
                });
            });
        }
    }

    @Override
    public List<Order> getAllOrderDetailsByStatus(String status, int pageIndex, int pageSize) {
        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);

        if (status.equalsIgnoreCase("all")) {
            return orderRepository.findAll(pageable).getContent();
        } else {
            List<Order> orders = orderRepository.findByStatus(status.toUpperCase());

            int startIndex = (int) pageable.getOffset();
            int endIndex = Math.min(startIndex + pageable.getPageSize(), orders.size());

            return orders.subList(startIndex, endIndex);
        }
    }

    @Override
    public List<Order> getOrderDetailsByUser(int pageIndex, int pageSize) throws CustomException {
        String token = jwtProvider.getTokenFromCookie(request);
        User user = userService.findUserProfileByJwt(token);

        List<Order> orders = orderRepository.findByUserId(user.getId());

        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);

        int startIndex = (int) pageable.getOffset();
        int endIndex = Math.min(startIndex + pageable.getPageSize(), orders.size());

        return orders.subList(startIndex, endIndex);
    }

    @Override
    @Transactional
    public String deleteOrderByUser(Long id) throws CustomException {
        String token = jwtProvider.getTokenFromCookie(request);
        User user = userService.findUserProfileByJwt(token);

        Optional<Order> order = orderRepository.findById(id);

        if (order.isPresent()) {

            if (Objects.equals(user.getId(), order.get().getUser().getId()) && order.get().getStatus().equalsIgnoreCase(OrderConstant.ORDER_PENDING)) {
                orderRepository.delete(order.get());

                return "Delete success !!!";
            } else {
                throw new CustomException("You do not have permission to delete this order !!!");
            }
        } else {
            return "Order not found with id " + id;
        }
    }

    @Override
    @Transactional
    public String deleteOrderByAdmin(Long id) throws CustomException {
        Optional<Order> order = orderRepository.findById(id);

        if (order.isPresent()) {
            orderRepository.delete(order.get());

            return "Delete success !!!";
        } else {
            return "Order not found with id " + id;
        }
    }

    @Override
    @Transactional
    public Order markOrderConfirmed(Long id) throws CustomException {
        Optional<Order> order = orderRepository.findById(id);

        if (order.isPresent()) {
            // cập nhật lại số lượng còn trong kho
            for (OrderLine orderLine : order.get().getOrderLines()) {
                Optional<Product> product = productRepository.findById(orderLine.getProduct().getId());

                if (product.isPresent()) {

                    Set<Size> sizes = new HashSet<>();
                    int quantity = 0;

                    for (Size s : product.get().getSizes()) {
                        if (orderLine.getSize() == s.getName()) {
                            s.setQuantity(s.getQuantity() - orderLine.getQuantity());
                            sizes.add(s);
                        } else {
                            sizes.add(s);
                        }
                        for (Size size : sizes) {
                            quantity += size.getQuantity();
                        }
                    }

                    product.get().setQuantity(quantity);
                    product.get().setSizes(sizes);

                    productRepository.save(product.get());
                } else {
                    throw new CustomException("Product not found with id: " + orderLine.getProduct().getId());
                }
            }

            order.get().setStatus(OrderConstant.ORDER_CONFIRMED);

            return orderRepository.save(order.get());
        } else {
            throw new CustomException("Order not found with id " + id);
        }
    }

    @Override
    @Transactional
    public Order markOrderShipped(Long id) throws CustomException {
        Optional<Order> order = orderRepository.findById(id);

        if (order.isPresent()) {
            order.get().setStatus(OrderConstant.ORDER_SHIPPED);

            return orderRepository.save(order.get());
        } else {
            throw new CustomException("Order not found with id " + id);
        }
    }

    @Override
    @Transactional
    public Order markOrderDelivered(Long id) throws CustomException {
        Optional<Order> order = orderRepository.findById(id);

        if (order.isPresent()) {
            order.get().setStatus(OrderConstant.ORDER_DELIVERED);
            order.get().setDeliveryDate(LocalDateTime.now());

            return orderRepository.save(order.get());
        } else {
            throw new CustomException("Order not found with id " + id);
        }
    }
}
