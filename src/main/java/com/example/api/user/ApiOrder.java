package com.example.api.user;

import com.example.exception.CustomException;
import com.example.request.OrderRequest;
import com.example.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("orderUser")
@RequestMapping("/api/user")
public class ApiOrder {
    @Autowired
    private OrderService orderDetailService;

    @GetMapping("/orderDetail")
    public ResponseEntity<?> getOrderDetail(@RequestParam("pageIndex")int pageIndex,
                                            @RequestParam("pageSize")int pageSize) throws CustomException {
        return new ResponseEntity<>(orderDetailService.getOrderDetailsByUser(pageIndex, pageSize), HttpStatus.OK);
    }

    @PostMapping("/checkout")
    public void placeOrder(@RequestBody OrderRequest orderRequest,
                           @RequestParam("isSingleProductBuyNow")Boolean isSingleProductBuyNow) throws CustomException {
        orderDetailService.placeOrder(orderRequest,isSingleProductBuyNow);
    }
}
