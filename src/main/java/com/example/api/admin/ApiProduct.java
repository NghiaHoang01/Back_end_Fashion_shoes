package com.example.api.admin;

import com.example.Entity.Product;
import com.example.exception.CustomException;
import com.example.request.ProductRequest;
import com.example.response.ProductResponse;
import com.example.response.Response;
import com.example.service.implement.ProductServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController("productOfRoleAdmin")
@RequestMapping("/api/admin")
public class ApiProduct {

    @Autowired
    private ProductServiceImpl productService;

    @PostMapping("/product")
    public ResponseEntity<?> createProduct(@RequestPart("product") ProductRequest productRequest,
                                           @RequestPart("mainImageFile") MultipartFile mainImageFile,
                                           @RequestPart("imageFile") MultipartFile[] multipartFiles) throws CustomException, IOException {
        Product product = productService.createProduct(productRequest,mainImageFile, multipartFiles);

        ProductResponse productResponse = new ProductResponse();
        productResponse.setProduct(product);
        productResponse.setMessage("Product created success !!!");
        productResponse.setSuccess(true);

        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @PutMapping("/product")
    public ResponseEntity<?> updateProduct(@RequestPart("product") ProductRequest productRequest, @RequestParam("id") Long id,
                                           @RequestPart("mainImageFile") MultipartFile mainImageFile,
                                           @RequestPart("imageFile") MultipartFile[] multipartFiles) throws CustomException, IOException {
        Product product = productService.updateProduct(id, productRequest,mainImageFile, multipartFiles);

        ProductResponse productResponse = new ProductResponse();
        productResponse.setProduct(product);
        productResponse.setMessage("Product updated success !!!");
        productResponse.setSuccess(true);

        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @DeleteMapping("/product")
    public ResponseEntity<?> deleteProduct(@RequestParam("id") Long id) throws CustomException {
        String message = productService.deleteProduct(id);

        Response response = new Response();
        response.setMessage(message);
        response.setSuccess(true);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
