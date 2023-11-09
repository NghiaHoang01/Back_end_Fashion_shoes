package com.example.repository;

import com.example.Entity.ChildCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChildCategoryRepository extends JpaRepository<ChildCategory,Long> {
    @Query("select c from ChildCategory c where c.name=?1 and c.parentCategoryOfChildCategory.name = ?2 and c.parentCategoryOfChildCategory.brandOfParentCategory.name = ?3")
    ChildCategory findByNameAndParentCategoryNameAndBrandName(String name,String parentCategoryName, String brandName);

    @Query("select c from ChildCategory c where c.parentCategoryOfChildCategory.name =?1 and c.parentCategoryOfChildCategory.brandOfParentCategory.name = ?2")
    List<ChildCategory> getAllChildCategoryByParentCategoryNameAndBrandName(String parentCategoryName, String brandName);
}
