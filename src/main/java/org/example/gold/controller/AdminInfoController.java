package org.example.gold.controller;

import lombok.RequiredArgsConstructor;
import org.example.gold.model.AdminInfo;
import org.example.gold.service.AdminInfoService;
import org.example.gold.service.ImageUploadUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/info")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AdminInfoController {

    private final AdminInfoService service;
    private final ImageUploadUtil imageUploadUtil;

    /** Get admin info */
    @GetMapping
    public ResponseEntity<AdminInfo> getAdminInfo() {
        AdminInfo info = service.getAdminInfo();
        return ResponseEntity.ok(info);
    }

    /**
     * Update admin info, including optional image uploads
     */
    @PutMapping
    public ResponseEntity<AdminInfo> updateAdminInfo(
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String orderEmails,
            @RequestParam(required = false) String tiktokLink,
            @RequestParam(required = false) String facebookLink,
            @RequestParam(required = false) String instagramLink,
            @RequestParam(value = "mainDivImage", required = false) MultipartFile mainDivImage,
            @RequestParam(value = "logoImage", required = false) MultipartFile logoImage
    ) {
        // Singleton instance
        AdminInfo adminInfo = service.getAdminInfo();

        // Update fields if provided
        if (phone != null) adminInfo.setPhone(phone);
        if (orderEmails != null) adminInfo.setOrderEmails(orderEmails);
        if (tiktokLink != null) adminInfo.setTiktokLink(tiktokLink);
        if (facebookLink != null) adminInfo.setFacebookLink(facebookLink);
        if (instagramLink != null) adminInfo.setInstagramLink(instagramLink);

        // Handle image uploads
        if (mainDivImage != null && !mainDivImage.isEmpty()) {
            String path = imageUploadUtil.saveImage(mainDivImage);
            adminInfo.setMainDivImage(path);
        }

        if (logoImage != null && !logoImage.isEmpty()) {
            String path = imageUploadUtil.saveImage(logoImage);
            adminInfo.setLogoImage(path);
        }

        // Save updates
        AdminInfo updated = service.save(adminInfo);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }
}
