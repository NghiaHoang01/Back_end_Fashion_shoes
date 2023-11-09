package com.example.util;

import com.example.Entity.ImageProduct;
import com.example.exception.CustomException;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Component
public class UploadImageUtil {
    public Set<ImageProduct> uploadImageOfProduct(MultipartFile[] multipartFiles) throws IOException, CustomException {
        Set<ImageProduct> imageProducts = new HashSet<>();

        for(MultipartFile file: multipartFiles){
            ImageProduct imageProduct = new ImageProduct();
            imageProduct.setName(file.getOriginalFilename());
            imageProduct.setType(file.getContentType());
            imageProduct.setImageToBase64(new String(Base64.encodeBase64(file.getBytes(),false)));

            imageProducts.add(imageProduct);
        }
        return imageProducts;
    }

    public ImageProduct uploadMainImageProduct(MultipartFile fileMainImage) throws IOException {
        ImageProduct mainImage = new ImageProduct();
        mainImage.setName(fileMainImage.getOriginalFilename());
        mainImage.setType(fileMainImage.getContentType());
        mainImage.setImageToBase64(new String(Base64.encodeBase64(fileMainImage.getBytes(),false)));

        return mainImage;
    }
}
