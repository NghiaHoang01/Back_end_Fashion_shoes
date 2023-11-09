package com.example.api;

import com.example.exception.CustomException;
import com.example.service.implement.ProductServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("product")
@RequestMapping("/api")
public class ApiProduct {

    @Autowired
    private ProductServiceImpl productService;

    @GetMapping("/products")
    public ResponseEntity<?> getAllProduct(@RequestParam("pageIndex") int pageIndex, @RequestParam("pageSize") int pageSize) {
        return new ResponseEntity<>(productService.getAllProduct(pageIndex,pageSize), HttpStatus.OK);
    }

    @GetMapping("/product/brand")
    public ResponseEntity<?> getProductByBrandName(@RequestParam("brand") String brandName, @RequestParam("pageIndex") int pageIndex,
                                                   @RequestParam("pageSize") int pageSize) throws CustomException {
        return new ResponseEntity<>(productService.findProductByBrand(brandName, pageIndex, pageSize), HttpStatus.OK);
    }

    @GetMapping("/product/parentCategory")
    public ResponseEntity<?> getProductByParentCategory(@RequestParam("brand") String brandName, @RequestParam("parentCategory") String parentCategory,
                                                        @RequestParam("pageIndex") int pageIndex,
                                                        @RequestParam("pageSize") int pageSize) throws CustomException {
        return new ResponseEntity<>(productService.findProductByParentCategory(brandName, parentCategory, pageIndex, pageSize), HttpStatus.OK);
    }

    @GetMapping("/product/childCategory")
    public ResponseEntity<?> getProductByChildCategory(@RequestParam("brand") String brandName, @RequestParam("parentCategory") String parentCategory,
                                                        @RequestParam("childCategory") String childCategory,
                                                        @RequestParam("pageIndex") int pageIndex,
                                                        @RequestParam("pageSize") int pageSize) throws CustomException {
        return new ResponseEntity<>(productService.findProductByChildCategory(brandName, parentCategory, childCategory, pageIndex, pageSize), HttpStatus.OK);
    }

    @GetMapping("/product/detail")
    public ResponseEntity<?> getDetailProduct(@RequestParam("id")Long id) throws CustomException {
        return new ResponseEntity<>(productService.getDetailProduct(id),HttpStatus.OK);
    }

    @GetMapping("/product/search")
    public ResponseEntity<?> getAllProductBySearch(@RequestParam("search")String search, @RequestParam("pageIndex")int pageIndex,@RequestParam("pageSize")int pageSize){
        return new ResponseEntity<>(productService.getAllProductBySearch(search,pageIndex,pageSize),HttpStatus.OK);
    }
}
