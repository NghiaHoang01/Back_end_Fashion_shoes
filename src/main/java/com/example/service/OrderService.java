package com.example.service;

import com.example.Entity.Order;
import com.example.exception.CustomException;
import com.example.request.OrderRequest;

import java.util.List;

public interface OrderService {
    void placeOrder(OrderRequest orderRequest, Boolean isSingleProductBuyNow) throws CustomException;

    List<Order> getAllOrderDetailsByStatus(String status, int pageIndex, int pageSize);

    List<Order> getOrderDetailsByUser(int pageIndex, int pageSize) throws CustomException;

    String deleteOrderByUser(Long id) throws CustomException;

    String deleteOrderByAdmin(Long id) throws CustomException;

    Order markOrderConfirmed(Long id) throws CustomException;

    Order markOrderShipped(Long id) throws CustomException;

    Order markOrderDelivered(Long id) throws CustomException;
}
