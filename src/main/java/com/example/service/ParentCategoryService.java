package com.example.service;

import com.example.Entity.ParentCategory;
import com.example.exception.CustomException;
import com.example.request.ParentCategoryRequest;

import java.util.List;
import java.util.Set;

public interface ParentCategoryService {
    ParentCategory createdParentCategory(ParentCategoryRequest parentCategoryRequest) throws CustomException;

    ParentCategory updateParentCategory(Long id, ParentCategoryRequest parentCategoryRequest) throws CustomException;

    String deleteParentCategory(Long id) throws CustomException;

    Set<ParentCategory> getAllParentCategoryByBrandName(String name) throws CustomException;

    List<ParentCategory> getAllParentCategory(int pageIndex, int pageSize);
}
