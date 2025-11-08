package org.example.gold.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class ImageUploadUtil {

    private final Cloudinary cloudinary;

    /**
     * Save uploaded images to Cloudinary and return their URLs.
     */
    public List<String> saveImages(MultipartFile[] images) {
        List<String> imageUrls = new ArrayList<>();

        for (MultipartFile file : images) {
            if (file == null || file.isEmpty()) continue; // تجنب null
            try {
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                        ObjectUtils.asMap("folder", "remotly_ecommerce"));
                imageUrls.add((String) uploadResult.get("secure_url"));
            } catch (IOException e) {
                log.error("Failed to upload image: {}", file.getOriginalFilename(), e);
                throw new RuntimeException("Failed to upload images", e);
            }
        }
        return imageUrls;
    }

    /**
     * Save a single image to Cloudinary and return its URL.
     */
    public String saveImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            return null;
        }
        return saveImages(new MultipartFile[]{image}).get(0);
    }
}
