package com.example.api;

import com.example.Entity.Brand;
import com.example.service.implement.BrandServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("brandOfUser")
@RequestMapping("/api")
public class ApiBrand {
    @Autowired
    private BrandServiceImpl brandService;

    @GetMapping("/brand")
    public ResponseEntity<?> getBrand(@RequestParam("pageIndex") int pageIndex, @RequestParam("pageSize")int pageSize){
        List<Brand> brands= brandService.getAllBrand(pageIndex,pageSize);
        return new ResponseEntity<>(brands, HttpStatus.OK);
    }
}
