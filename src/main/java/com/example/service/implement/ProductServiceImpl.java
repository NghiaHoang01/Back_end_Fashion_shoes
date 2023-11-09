package com.example.service.implement;

import com.example.Entity.*;
import com.example.config.JwtProvider;
import com.example.exception.CustomException;
import com.example.repository.BrandRepository;
import com.example.repository.ChildCategoryRepository;
import com.example.repository.ParentCategoryRepository;
import com.example.repository.ProductRepository;
import com.example.request.ProductRequest;
import com.example.service.ProductService;
import com.example.util.UploadImageUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private ParentCategoryRepository parentCategoryRepository;
    @Autowired
    private ChildCategoryRepository childCategoryRepository;
    @Autowired
    private UploadImageUtil uploadImageUtil;

    @Override
    @Transactional
    public Product createProduct(ProductRequest productRequest, MultipartFile fileMainImage, MultipartFile[] multipartFiles) throws CustomException, IOException {
        productRequest.setBrand(productRequest.getBrand().toUpperCase());
        Brand checkBrand = brandRepository.findByName(productRequest.getBrand());

        if (checkBrand != null) {
            productRequest.setParentCategory(productRequest.getParentCategory().toUpperCase());
            ParentCategory checkParentCategory = parentCategoryRepository.findByNameAndBrandName(productRequest.getParentCategory(), checkBrand.getName());

            if (checkParentCategory != null) {
                productRequest.setChildCategory(productRequest.getChildCategory().toUpperCase());
                ChildCategory checkChildCategory = childCategoryRepository.findByNameAndParentCategoryNameAndBrandName(productRequest.getChildCategory(),
                        checkParentCategory.getName(), checkBrand.getName());

                if (checkChildCategory != null) {
                    String token = jwtProvider.getTokenFromCookie(request);
                    User admin = userService.findUserProfileByJwt(token);

                    int quantity = 0;

                    ImageProduct mainImage = uploadImageUtil.uploadMainImageProduct(fileMainImage);
                    Set<ImageProduct> mainImageProducts = new HashSet<>();
                    mainImageProducts.add(mainImage);
                    Set<ImageProduct> imageProducts = uploadImageUtil.uploadImageOfProduct(multipartFiles);

                    Product product = new Product();
                    product.setCreatedBy(admin.getId());
                    product.setTitle(productRequest.getTitle());
                    product.setDescription(productRequest.getDescription());
                    product.setDiscountedPercent(productRequest.getDiscountedPercent());
                    product.setDiscountedPrice(productRequest.getPrice() - ((double) productRequest.getDiscountedPercent() / 100) * productRequest.getPrice());
                    product.setName(productRequest.getName());
                    product.setMainImage(mainImageProducts);
                    product.setPrice(productRequest.getPrice());
                    for (Size s : productRequest.getSizes()) {
                        quantity += s.getQuantity();
                    }
                    product.setQuantity(quantity);
                    product.setBrandProduct(checkBrand);
                    product.setParentCategoryOfProduct(checkParentCategory);
                    product.setChildCategoryOfProduct(checkChildCategory);
                    product.setSizes(productRequest.getSizes());
                    product.setColor(productRequest.getColors().toUpperCase());
                    product.setImageProducts(imageProducts);
                    return productRepository.save(product);

                } else {
                    throw new CustomException("Child category with name " + productRequest.getChildCategory() + " not exist !!!");
                }
            } else {
                throw new CustomException("Parent category with name " + productRequest.getParentCategory() + " not exist !!!");
            }
        } else {
            throw new CustomException("Brand not found with name: " + productRequest.getBrand());
        }
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, ProductRequest productRequest, MultipartFile fileMainImage, MultipartFile[] multipartFiles) throws CustomException, IOException {
        Optional<Product> oldProduct = productRepository.findById(id);

        if (oldProduct.isPresent()) {
            productRequest.setBrand(productRequest.getBrand().toUpperCase());

            Brand checkBrand = brandRepository.findByName(productRequest.getBrand());

            if (checkBrand != null) {
                productRequest.setParentCategory(productRequest.getParentCategory().toUpperCase());

                ParentCategory checkParentCategory = parentCategoryRepository.findByNameAndBrandName(productRequest.getParentCategory(), checkBrand.getName());

                if (checkParentCategory != null) {
                    productRequest.setChildCategory(productRequest.getChildCategory().toUpperCase());

                    ChildCategory checkChildCategory = childCategoryRepository.findByNameAndParentCategoryNameAndBrandName(productRequest.getChildCategory(),
                            checkParentCategory.getName(), checkBrand.getName());

                    if (checkChildCategory != null) {
                        String token = jwtProvider.getTokenFromCookie(request);
                        User admin = userService.findUserProfileByJwt(token);

                        int quantity = 0;

                        ImageProduct mainImage = uploadImageUtil.uploadMainImageProduct(fileMainImage);
                        Set<ImageProduct> mainImageProducts = new HashSet<>();
                        mainImageProducts.add(mainImage);
                        Set<ImageProduct> imageProducts = uploadImageUtil.uploadImageOfProduct(multipartFiles);

                        oldProduct.get().setUpdateBy(admin.getId());
                        oldProduct.get().setTitle(productRequest.getTitle());
                        oldProduct.get().setDescription(productRequest.getDescription());
                        oldProduct.get().setDiscountedPercent(productRequest.getDiscountedPercent());
                        oldProduct.get().setDiscountedPrice(productRequest.getPrice() - ((double) productRequest.getDiscountedPercent() / 100) * productRequest.getPrice());
                        oldProduct.get().setMainImage(mainImageProducts);
                        oldProduct.get().setName(productRequest.getName());
                        oldProduct.get().setPrice(productRequest.getPrice());
                        for (Size s : productRequest.getSizes()) {
                            quantity += s.getQuantity();
                        }
                        oldProduct.get().setQuantity(quantity);
                        oldProduct.get().setBrandProduct(checkBrand);
                        oldProduct.get().setParentCategoryOfProduct(checkParentCategory);
                        oldProduct.get().setChildCategoryOfProduct(checkChildCategory);
                        oldProduct.get().setSizes(productRequest.getSizes());
                        oldProduct.get().setColor(productRequest.getColors().toUpperCase());
                        oldProduct.get().setImageProducts(imageProducts);

                        return productRepository.save(oldProduct.get());
                    } else {
                        throw new CustomException("Child category not found with name: " + productRequest.getChildCategory());
                    }
                } else {
                    throw new CustomException("Parent category not found with name: " + productRequest.getParentCategory());
                }
            } else {
                throw new CustomException("Brand not found with name: " + productRequest.getBrand());
            }
        } else {
            throw new CustomException("Product not found with id: " + id);
        }
    }

    @Override
    @Transactional
    public String deleteProduct(Long id) throws CustomException {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            productRepository.delete(product.get());
            return "Delete success !!!";
        } else {
            return "Product not found with id: " + id;
        }
    }

    @Override
    public List<Product> findProductByBrand(String brand, int pageIndex, int pageSize) throws CustomException {
        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);

        List<Product> products = productRepository.getAllProductByBrandName(brand);
        int startIndex = (int) pageable.getOffset();
        int endIndex = Math.min(startIndex + pageable.getPageSize(), products.size());

        return products.subList(startIndex, endIndex);
    }

    @Override
    public List<Product> findProductByParentCategory(String brand, String parentCategory, int pageIndex, int pageSize) throws CustomException {
        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);

        List<Product> products = productRepository.getAllProductByParentCategory(brand, parentCategory);

        int startIndex = (int) pageable.getOffset();
        int endIndex = Math.min(startIndex + pageable.getPageSize(), products.size());

        return products.subList(startIndex, endIndex);
    }

    @Override
    public List<Product> findProductByChildCategory(String brand, String parentCategory, String childCategory, int pageIndex, int pageSize) throws CustomException {
        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);

        List<Product> products = productRepository.getAllProductByChildCategory(brand, parentCategory, childCategory);

        int startIndex = (int) pageable.getOffset();
        int endIndex = Math.min(startIndex + pageable.getPageSize(), products.size());

        return products.subList(startIndex, endIndex);
    }

    @Override
    public List<Product> getAllProduct(int pageIndex, int pageSize) {
        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);

        List<Product> products = productRepository.findAllByOrderByIdDesc();

        int startIndex = (int) pageable.getOffset();
        int endIndex = Math.min(startIndex + pageable.getPageSize(), products.size());

        return products.subList(startIndex, endIndex);
    }

    @Override
    public List<Product> filterProduct(String brand, String parentCategory, String childCategory, String sort,
                                       double minPrice, double maxPrice, boolean sale, List<String> colors, int pageIndex, int pageSize) {
        Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);
        List<Product> products = productRepository.filterProducts(brand, parentCategory, childCategory, minPrice, maxPrice, sort);
        if (sale) {
            products = products.stream().filter(p -> p.getDiscountedPercent() > 0).collect(Collectors.toList());
        } else {
            products = products.stream().filter(p -> p.getDiscountedPercent() == 0).collect(Collectors.toList());
        }

        if (!colors.isEmpty()) {
            products = products.stream().filter(p -> colors.stream().anyMatch(c -> c.equalsIgnoreCase(p.getColor()))).collect(Collectors.toList());
        }

        int startIndex = (int) pageable.getOffset();
        int endIndex = Math.min(startIndex + pageable.getPageSize(), products.size());

        return products.subList(startIndex, endIndex);
    }

    @Override
    public List<Product> getAllProductBySearch(String search, int pageIndex, int pageSize) {
        if ((search.equals(""))) {
            return getAllProduct(pageIndex, pageSize);
        } else {
            Pageable pageable = PageRequest.of(pageIndex - 1, pageSize);

            List<Product> products = productRepository.getAllProductBySearch(search);

            int startIndex = (int) pageable.getOffset();
            int endIndex = Math.min(startIndex + pageable.getPageSize(), products.size());

            return products.subList(startIndex, endIndex);
        }
    }

    @Override
    public Product getDetailProduct(Long id) throws CustomException {
        Optional<Product> product = productRepository.findById(id);
        return product.orElseThrow(() -> new CustomException("Product not found with id: " + id));
    }

    @Override
    public int countProductByBrand(String brandName) {
        return productRepository.countProductByBrandName(brandName);
    }

    @Override
    public int countProductByBrandAndParentCategory(String brandName, String parentCategory) {
        return productRepository.countProductByBrandNameAndParentCategory(brandName, parentCategory);
    }

    @Override
    public int countAllProducts() {
        return (int) productRepository.count();
    }

    @Override
    public int countProductBySearch(String search) {
        return productRepository.countProductBySearch(search);
    }

    @Override
    public Product getLastProduct() {
        return productRepository.findTop1ByOrderByIdDesc();
    }
}
