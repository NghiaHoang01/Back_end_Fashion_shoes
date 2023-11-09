package com.example.service;

import com.example.Entity.Product;
import com.example.exception.CustomException;
import com.example.request.ProductRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    Product createProduct(ProductRequest productRequest, MultipartFile fileMainImage, MultipartFile[] multipartFiles) throws CustomException, IOException;
    Product updateProduct(Long id, ProductRequest productRequest, MultipartFile fileMainImage, MultipartFile[] multipartFiles) throws CustomException, IOException;
    String deleteProduct(Long id) throws CustomException;
    List<Product> findProductByBrand(String brand, int pageIndex, int pageSize) throws CustomException;
    List<Product> findProductByParentCategory(String brand, String parentCategory, int pageIndex, int pageSize) throws CustomException;
    List<Product> findProductByChildCategory(String brand, String parentCategory, String childCategory, int pageIndex, int pageSize) throws CustomException;
    List<Product> getAllProduct(int pageIndex, int pageSize);
    List<Product> filterProduct(String brand, String parentCategory, String childCategory,
                                String sort, double minPrice, double maxPrice, boolean sale, List<String> colors, int pageIndex, int pageSize);
    List<Product> getAllProductBySearch(String search,int pageIndex,int pageSize);

    Product getDetailProduct(Long id) throws CustomException;

    Product getLastProduct();

    int countProductByBrand(String brandName);

    int countProductByBrandAndParentCategory(String brandName, String parentCategory);

    int countAllProducts();

    int countProductBySearch(String search);
}
