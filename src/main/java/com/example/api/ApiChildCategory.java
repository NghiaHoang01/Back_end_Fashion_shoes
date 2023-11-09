package com.example.api;

import com.example.Entity.ChildCategory;
import com.example.exception.CustomException;
import com.example.service.implement.ChildCategoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("childCategory")
@RequestMapping("/api")
public class ApiChildCategory {
    @Autowired
    private ChildCategoryServiceImpl childCategoryService;

    @GetMapping("/childCategory")
    public ResponseEntity<?> getChildCategoryByParentCategoryNameAndBrandName(@RequestParam("parentName") String parentName,
                                                                              @RequestParam("brand") String brand) throws CustomException {
        List<ChildCategory> childCategories = childCategoryService.getAllChildCategoryByParentCategoryNameAndBrandName(parentName, brand);

        return new ResponseEntity<>(childCategories, HttpStatus.OK);
    }
}
