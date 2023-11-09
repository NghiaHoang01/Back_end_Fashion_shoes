package com.example.repository;

import com.example.Entity.ParentCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface ParentCategoryRepository extends JpaRepository<ParentCategory,Long> {
    @Query("select p from ParentCategory p where p.name = ?1 and p.brandOfParentCategory.name = ?2")
    ParentCategory findByNameAndBrandName(String name,String brandName);

    @Query("select p from ParentCategory p where p.brandOfParentCategory.name = ?1")
    Set<ParentCategory> getAllParentCategoryByBrandName(String name);

}
