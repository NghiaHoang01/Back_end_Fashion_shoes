package com.example.service.implement;

import com.example.Entity.*;
import com.example.config.JwtProvider;
import com.example.config.VNPayConfig;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
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
            order.setPay(OrderConstant.ORDER_UNPAID);
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
    public String placeOrderVnPay(long totalPrice, String orderInfo, String orderId) {

        String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", VNPayConfig.vnp_Version);
        vnp_Params.put("vnp_Command", VNPayConfig.vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(totalPrice * 100));
        vnp_Params.put("vnp_CurrCode", "VND");

        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", orderInfo + "-" + orderId);
        vnp_Params.put("vnp_OrderType", VNPayConfig.orderType);

        String locate = "vn";
        vnp_Params.put("vnp_Locale", locate);

        vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", VNPayConfig.vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                try {
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    //Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

        return VNPayConfig.vnp_PayUrl + "?" + queryUrl;
    }

    @Override
    public List<OrderResponse> getOrderResponses(List<Order> orders) {
        List<OrderResponse> orderResponseList = new ArrayList<>();

        orders.forEach(order -> {
            OrderResponse orderResponse = new OrderResponse();

            orderResponse.setId(order.getId());
            orderResponse.setFullName(order.getFullName());
            orderResponse.setEmail(order.getCreatedBy());
            orderResponse.setPay(order.getPay());
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
    public List<OrderResponse> getOrderDetailsByUser(String orderStatus, String paymentMethod,
                                                     LocalDateTime orderDateStart, LocalDateTime orderDateEnd,
                                                     LocalDateTime deliveryDateStart, LocalDateTime deliveryDateEnd,
                                                     LocalDateTime receivingDateStart, LocalDateTime receivingDateEnd) throws CustomException {
        String token = jwtProvider.getTokenFromCookie(request);
        User user = userService.findUserProfileByJwt(token);

        List<Order> ordersOfUser = orderRepository.getOrdersByUser(user.getId(), orderStatus, paymentMethod,
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
            order.get().setPay(OrderConstant.ORDER_PAID);
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
    public long findOrderIdNewest() {
        return orderRepository.getOrderIdNewest();
    }

    @Override
    public void updatePayOfOrderVNPay(String vnp_ResponseCode, Long orderId) throws CustomException {
        Optional<Order> order = orderRepository.findById(orderId);
        if(order.isPresent()){
            if(vnp_ResponseCode.equals("00")){
                order.get().setPay(OrderConstant.ORDER_PAID);
            }else{
                order.get().setPay(OrderConstant.ORDER_UNPAID);
            }
            orderRepository.save(order.get());
        }else{
            throw new CustomException("Order not found !!!");
        }
    }

}
