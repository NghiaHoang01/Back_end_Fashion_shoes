package com.example.repository;

import com.example.Entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand,Long> {
    Brand findByName(String name);
}
