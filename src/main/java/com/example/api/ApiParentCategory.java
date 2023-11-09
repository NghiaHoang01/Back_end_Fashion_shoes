package com.example.api;

import com.example.Entity.ParentCategory;
import com.example.exception.CustomException;
import com.example.service.implement.ParentCategoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController("parentCategory")
@RequestMapping("/api")
public class ApiParentCategory {
    @Autowired
    private ParentCategoryServiceImpl parentCategoryService;

    @GetMapping("/parentCategory")
    public ResponseEntity<?> getParentCategoryByBrandName(@RequestParam("brand") String brandName) throws CustomException {
        Set<ParentCategory> parentCategories = parentCategoryService.getAllParentCategoryByBrandName(brandName.toUpperCase());
        return new ResponseEntity<>(parentCategories, HttpStatus.OK);
    }
}
