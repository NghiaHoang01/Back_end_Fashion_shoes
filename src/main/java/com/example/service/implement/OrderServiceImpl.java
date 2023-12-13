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
import com.example.response.OrderLineResponse;
import com.example.response.ListOrderResponse;
import com.example.response.OrderResponse;
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
    public void placeOrderCOD(OrderRequest orderRequest) throws CustomException {
        // nếu số lượng sản phẩm order nhiều hơn số lượng sản phẩm có trong kho thì
        // nhân viên phải liên hệ khách hàng để thỏa thuận lại về đơn hàng

        // do mua hàng thông qua cart nên phải check xem các sản phẩm request có đang nằm trong giỏ hàng của user không !!!
        // sau khi đặt hàng thành công thì sẽ xóa các sản phẩm đã đặt ở trong giỏ hàng của user

        String token = jwtProvider.getTokenFromCookie(request);
        User user = userService.findUserProfileByJwt(token);

        List<OrderProductQuantityRequest> orderProductQuantityRequests = orderRequest.getProductQuantities();

        List<Cart> cartOfUser = cartRepository.findByUserIdOrderByIdDesc(user.getId());

        List<Boolean> checkExists = new ArrayList<>();

        orderProductQuantityRequests.forEach(p -> {
            checkExists.add(cartOfUser.stream().anyMatch(c -> (Objects.equals(c.getProduct().getId(), p.getProductId()) && (c.getSize() == p.getSize()))));
        });

        boolean existItem = checkExists.stream().anyMatch(exist -> !exist);

        if (existItem) {
            throw new CustomException("Some products not exits in your cart, Please check again and follow the correct steps !!!");
        } else {
            double totalPrice = 0;
            // create order
            Order order = new Order();
            order.setAddress(orderRequest.getAddress());
            order.setDistrict(orderRequest.getDistrict());
            order.setProvince(orderRequest.getProvince());
            order.setWard(orderRequest.getWard());
            order.setFullName(orderRequest.getFullName());
            order.setUser(user);
            order.setAlternatePhoneNumber(orderRequest.getAlternatePhoneNumber());
            order.setPhoneNumber(orderRequest.getPhoneNumber());
            order.setStatus(OrderConstant.ORDER_PENDING);
            order.setTransportFee(orderRequest.getTransportFee());
            order.setCreatedBy(user.getEmail());
            for (OrderProductQuantityRequest p : orderProductQuantityRequests) {
                totalPrice += p.getTotalPrice();
            }
            order.setTotalPrice(totalPrice + order.getTransportFee());
            order.setNote(orderRequest.getNote());
            order.setPaymentMethod(orderRequest.getPaymentMethod());
            order.setOrderDate(LocalDateTime.now());
            order = orderRepository.save(order); // => order success

            // create order line
            for (OrderProductQuantityRequest productOrder : orderProductQuantityRequests) {
                Optional<Product> product = productRepository.findById(productOrder.getProductId());

                if (product.isPresent()) {
                    OrderLine orderLine = new OrderLine();

                    orderLine.setOrder(order);
                    orderLine.setProduct(product.get());
                    orderLine.setQuantity(productOrder.getQuantity());
                    orderLine.setSize(productOrder.getSize());
                    orderLine.setCreatedBy(user.getEmail());
                    orderLine.setTotalPrice(productOrder.getTotalPrice());

                    orderLineRepository.save(orderLine);
                } else {
                    throw new CustomException("Product not found !!!");
                }
            }

            // delete product in cart of user
            cartOfUser.forEach(c -> {
                boolean check = orderProductQuantityRequests.stream().anyMatch(p -> (p.getSize() == c.getSize() && Objects.equals(p.getProductId(), c.getProduct().getId())));
                if (check) {
                    cartRepository.delete(c);
                }
            });
        }
    }

    @Override
    public List<OrderResponse> getOrderResponses(List<Order> orders) {
        List<OrderResponse> orderResponseList = new ArrayList<>();

        orders.forEach(order -> {
            OrderResponse orderResponse = new OrderResponse();

            orderResponse.setId(order.getId());
            orderResponse.setFullName(order.getFullName());
            orderResponse.setEmail(order.getCreatedBy());
            orderResponse.setPhoneNumber(order.getPhoneNumber());
            orderResponse.setAlternatePhone(order.getAlternatePhoneNumber());
            orderResponse.setAddress(order.getAddress());
            orderResponse.setWard(order.getWard());
            orderResponse.setDistrict(order.getDistrict());
            orderResponse.setProvince(order.getProvince());
            orderResponse.setNotes(order.getNote());
            orderResponse.setDeliveryDate(order.getDeliveryDate());
            orderResponse.setReceivingDate(order.getReceivingDate());
            orderResponse.setPaymentMethod(order.getPaymentMethod());
            orderResponse.setStatusOrder(order.getStatus());
            orderResponse.setTotalPrice(order.getTotalPrice());
            orderResponse.setTransportFee(order.getTransportFee());
            orderResponse.setOrderDate(order.getOrderDate());
            List<OrderLineResponse> orderLineResponseList = new ArrayList<>();

            List<OrderLine> orderLines = orderLineRepository.findByOrderId(order.getId());

            orderLines.forEach(orderLine -> {
                Optional<Product> product = productRepository.findById(orderLine.getProduct().getId());

                OrderLineResponse orderLineResponse = new OrderLineResponse();

                orderLineResponse.setProductId(product.get().getId());
                orderLineResponse.setBrand(product.get().getBrandProduct().getName());
                orderLineResponse.setMainImageBase64(product.get().getMainImageBase64());
                orderLineResponse.setQuantity(orderLine.getQuantity());
                orderLineResponse.setSize(orderLine.getSize());
                orderLineResponse.setNameProduct(product.get().getName());
                orderLineResponse.setTotalPrice(orderLine.getTotalPrice());

                orderLineResponseList.add(orderLineResponse);
            });

            orderResponse.setOrderLines(orderLineResponseList);

            orderResponseList.add(orderResponse);
        });
        return orderResponseList;
    }

    @Override
    public List<OrderResponse> getOrderDetailsByUser(String orderStatus,String paymentMethod,
                                                     LocalDateTime orderDateStart, LocalDateTime orderDateEnd,
                                                     LocalDateTime deliveryDateStart, LocalDateTime deliveryDateEnd,
                                                     LocalDateTime receivingDateStart, LocalDateTime receivingDateEnd) throws CustomException {
        String token = jwtProvider.getTokenFromCookie(request);
        User user = userService.findUserProfileByJwt(token);

        List<Order> ordersOfUser = orderRepository.getOrdersByUser(user.getId(), orderStatus,paymentMethod,
                orderDateStart, orderDateEnd,
                deliveryDateStart, deliveryDateEnd,
                receivingDateStart, receivingDateEnd);

        return getOrderResponses(ordersOfUser);
    }

    @Override
    public ListOrderResponse getAllOrderDetailByAdmin(String orderBy, String phoneNumber, String orderStatus, String paymentMethod,
                                                      String province, String district, String ward,
                                                      LocalDateTime orderDateStart, LocalDateTime orderDateEnd,
                                                      LocalDateTime deliveryDateStart, LocalDateTime deliveryDateEnd,
                                                      LocalDateTime receivingDateStart, LocalDateTime receivingDateEnd,
                                                      int pageIndex, int pageSize) {

        List<Order> orderList = orderRepository.getOrdersByAdmin(orderBy, phoneNumber, orderStatus, paymentMethod, province, district,
                ward, orderDateStart, orderDateEnd, deliveryDateStart, deliveryDateEnd, receivingDateStart, receivingDateEnd);

        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);
        int startIndex = (int) pageable.getOffset();
        int endIndex = Math.min(startIndex + pageable.getPageSize(), orderList.size());

        ListOrderResponse listOrderResponse = new ListOrderResponse();
        listOrderResponse.setListOrders(getOrderResponses(orderList.subList(startIndex, endIndex)));
        listOrderResponse.setTotal((long) orderList.size());

        return listOrderResponse;
    }

    @Override
    @Transactional
    public void cancelOrderByUser(Long idOrder) throws CustomException {
        String token = jwtProvider.getTokenFromCookie(request);
        User user = userService.findUserProfileByJwt(token);

        Optional<Order> order = orderRepository.findById(idOrder);

        if (order.isPresent()) {
            if (order.get().getStatus().equals(OrderConstant.ORDER_PENDING)) {
                // Kiểm tra xem user có sở hữu đơn hàng này không
                if (Objects.equals(user.getId(), order.get().getUser().getId())) {
                    orderRepository.delete(order.get());
                } else {
                    throw new CustomException("You do not have permission to delete this order !!!");
                }
            } else {
                throw new CustomException("This order is on its way to you !!!");
            }

        } else {
            throw new CustomException("Not found this order !!!");
        }
    }

    @Override
    @Transactional
    public void markOrderConfirmed(Long id) throws CustomException {
        Optional<Order> order = orderRepository.findById(id);

        if (order.isPresent()) {
            // cập nhật lại số lượng còn trong kho
            order.get().getOrderLines().forEach(orderLine -> {
                Optional<Product> product = productRepository.findById(orderLine.getProduct().getId());

                if (product.isPresent()) {

                    Set<Size> sizes = new HashSet<>();
                    int quantity = 0;

                    boolean checkSizeExist = false;  // => kiểm tra xem size này có tồn tại không

                    // cập nhật lại số lượng tùng size
                    for (Size size : product.get().getSizes()) {
                        if (size.getName() == orderLine.getSize()) {
                            size.setQuantity(size.getQuantity() - orderLine.getQuantity());
                            sizes.add(size);
                            checkSizeExist = true;
                        }
                        sizes.add(size);
                    }

                    if (!checkSizeExist) {
                        try {
                            throw new CustomException("Product " + product.get().getId() + " not have size " + orderLine.getSize() + " !!!");
                        } catch (CustomException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    // cập nhật lại tổng số lượng của sản phẩm
                    for (Size size : sizes) {
                        quantity += size.getQuantity();
                    }

                    product.get().setQuantity(quantity);
                    product.get().setSizes(sizes);

                    productRepository.save(product.get());
                } else {
                    try {
                        throw new CustomException("Product not found with id: " + orderLine.getProduct().getId());
                    } catch (CustomException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            order.get().setStatus(OrderConstant.ORDER_CONFIRMED);

            orderRepository.save(order.get());
        } else {
            throw new CustomException("Order not found with id " + id);
        }
    }

    @Override
    @Transactional
    public void markOrderShipped(Long id) throws CustomException {
        Optional<Order> order = orderRepository.findById(id);

        if (order.isPresent()) {
            order.get().setStatus(OrderConstant.ORDER_SHIPPED);
            order.get().setDeliveryDate(LocalDateTime.now());

            orderRepository.save(order.get());
        } else {
            throw new CustomException("Order not found with id " + id);
        }
    }

    @Override
    @Transactional
    public void markOrderDelivered(Long id) throws CustomException {
        Optional<Order> order = orderRepository.findById(id);

        if (order.isPresent()) {
            order.get().setStatus(OrderConstant.ORDER_DELIVERED);
            order.get().setReceivingDate(LocalDateTime.now());

            orderRepository.save(order.get());
        } else {
            throw new CustomException("Order not found with id " + id);
        }
    }

    @Override
    @Transactional
    public void deleteOrderByAdmin(Long id) throws CustomException {
        Optional<Order> order = orderRepository.findById(id);

        if (order.isPresent()) {
            orderRepository.delete(order.get());
        } else {
            throw new CustomException("Not found order have id: " + id + " !!!");
        }
    }

    @Override
    @Transactional
    public void deleteSomeOrdersByAdmin(List<Long> listIdOrder) throws CustomException {
        for (Long id : listIdOrder) {
            Optional<Order> order = orderRepository.findById(id);
            if (order.isPresent()) {
                orderRepository.delete(order.get());
            } else {
                throw new CustomException("Not found order have id " + id + " !!!");
            }
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


}
