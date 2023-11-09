package com.example.repository;

import com.example.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("select p from Product p where p.brandProduct = ?1 and " +
            "p.parentCategoryOfProduct.name = ?2 and " +
            "p.childCategoryOfProduct.name = ?3 and " +
            "((?4 is null and ?5 is null ) or (p.discountedPrice between ?4 and ?5))" +
            "order by " +
            "case when ?6 = 'price_low' then p.discountedPrice end asc," +
            "case when ?6 = 'price_hight' then p.discountedPrice end desc," +
            "case when ?6 = 'newest' then p.id end desc")
    List<Product> filterProducts(String brand, String parentCategory, String childCategory,
                                 double minPrice, double maxPrice, String sort);

    @Query("select p from Product p where p.brandProduct.name = ?1 order by p.id desc")
    List<Product> getAllProductByBrandName(String brandName);

    @Query("select p from Product p where p.brandProduct.name = ?1 and p.parentCategoryOfProduct.name = ?2 order by p.id desc")
    List<Product> getAllProductByParentCategory(String brand, String parentCategory);

    @Query("select p from Product p where p.brandProduct.name = ?1 and p.parentCategoryOfProduct.name = ?2 and p.childCategoryOfProduct.name = ?3 order by p.id desc")
    List<Product> getAllProductByChildCategory(String brand, String parentCategory, String childCategory);
    List<Product> findAllByOrderByIdDesc();

    Product findTop1ByOrderByIdDesc();
    @Query("select p from Product p where p.name like %?1% order by p.id desc")
    List<Product> getAllProductBySearch(String search);

    @Query("select count(*) from Product p where p.brandProduct.name = ?1")
    int countProductByBrandName(String brandName);

    @Query("select count(*) from Product p where p.brandProduct.name = ?1 and p.parentCategoryOfProduct = ?2")
    int countProductByBrandNameAndParentCategory(String brandName, String parentCategory);

    @Query("select count(*) from Product p where p.name like %?1%")
    int countProductBySearch(String search);
}
