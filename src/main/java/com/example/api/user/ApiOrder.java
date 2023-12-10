package com.example.api.user;

import com.example.exception.CustomException;
import com.example.request.OrderRequest;
import com.example.response.OrderResponse;
import com.example.response.Response;
import com.example.response.ResponseData;
import com.example.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController("orderUser")
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:3000/", allowCredentials = "true")
public class ApiOrder {
    @Autowired
    private OrderService orderDetailService;

    // CALL SUCCESS
    @GetMapping("/orders/detail")
    public ResponseEntity<?> getOrderDetail(@RequestParam(value = "orderStatus", required = false) String orderStatus,
                                            @RequestParam(value = "paymentMethod", required = false) String paymentMethod,
                                            @RequestParam(value = "orderDateStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime orderDateStart,
                                            @RequestParam(value = "orderDateEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime orderDateEnd,
                                            @RequestParam(value = "deliveryDateStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime deliveryDateStart,
                                            @RequestParam(value = "deliveryDateEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime deliveryDateEnd,
                                            @RequestParam(value = "receivingDateStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime receivingDateStart,
                                            @RequestParam(value = "receivingDateEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime receivingDateEnd) throws CustomException {
        List<OrderResponse> orderResponses = orderDetailService.getOrderDetailsByUser(orderStatus,paymentMethod, orderDateStart, orderDateEnd,
                deliveryDateStart, deliveryDateEnd, receivingDateStart, receivingDateEnd);

        ResponseData<List<OrderResponse>> responseData = new ResponseData<>();
        responseData.setSuccess(true);
        responseData.setResults(orderResponses);
        responseData.setMessage("Get all orders of user success !!!");

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    // CALL SUCCESS
    @PostMapping("/place/order/cod")
    public ResponseEntity<?> placeOrder(@RequestBody OrderRequest orderRequest) throws CustomException {
        orderDetailService.placeOrderCOD(orderRequest);

        Response response = new Response();
        response.setSuccess(true);
        response.setMessage("Place order success !!!");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // CALL SUCCESS
    @DeleteMapping("/order")
    public ResponseEntity<?> cancelOrderByUser(@RequestParam("id") Long idOrder) throws CustomException {
        orderDetailService.cancelOrderByUser(idOrder);

        Response response = new Response();
        response.setMessage("This order is cancel success !!!");
        response.setSuccess(true);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
