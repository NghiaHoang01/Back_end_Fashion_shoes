package com.example.service;

import com.example.Entity.Order;
import com.example.exception.CustomException;
import com.example.request.OrderRequest;
import com.example.response.OrderListResponse;
import com.example.response.OrderResponse;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface OrderService {
    void placeOrderCOD(OrderRequest orderRequest) throws CustomException;
    List<OrderResponse> getOrderResponses(List<Order> orders);

    List<OrderResponse> getOrderDetailsByUser(String orderStatus,String paymentMethod,
                                              LocalDateTime orderDateStart, LocalDateTime orderDateEnd,
                                              LocalDateTime deliveryDateStart, LocalDateTime deliveryDateEnd,
                                              LocalDateTime receivingDateStart, LocalDateTime receivingDateEnd) throws CustomException;

    OrderListResponse getAllOrderDetailByAdmin(String orderBy, String phoneNumber, String orderStatus, String paymentMethod,
                                               String province, String district, String ward,
                                               LocalDateTime orderDateStart, LocalDateTime orderDateEnd,
                                               LocalDateTime deliveryDateStart, LocalDateTime deliveryDateEnd,
                                               LocalDateTime receivingDateStart, LocalDateTime receivingDateEnd,
                                               int pageIndex, int pageSize);
    void cancelOrderByUser(Long idOrder) throws CustomException;

    void markOrderShipped(Long id) throws CustomException;

    void markOrderConfirmed(Long id) throws CustomException;

    void markOrderDelivered(Long id) throws CustomException;

    void deleteOrderByAdmin(Long id) throws CustomException;

    void deleteSomeOrdersByAdmin(List<Long> listIdOrder) throws CustomException;

    List<Order> getAllOrderDetailsByStatus(String status, int pageIndex, int pageSize);



}
