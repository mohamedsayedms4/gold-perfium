package org.example.gold.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.gold.model.AdminInfo;
import org.example.gold.repository.AdminInfoRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminInfoService {

    private final AdminInfoRepository repository;

    /** Singleton instance of AdminInfo */
    private AdminInfo adminInfoInstance;

    /**
     * Initialize the singleton AdminInfo after bean creation
     */
    @PostConstruct
    public void init() {
        // Try to load existing AdminInfo from DB
        adminInfoInstance = repository.findAll()
                .stream()
                .findFirst()
                .orElseGet(() -> {
                    // If none exists, create a new one and save
                    AdminInfo info = new AdminInfo();
                    return repository.save(info);
                });
    }

    /** Get the singleton AdminInfo instance */
    public AdminInfo getAdminInfo() {
        return adminInfoInstance;
    }

    /** Update the singleton AdminInfo instance and save changes to DB */
    public AdminInfo save(AdminInfo adminInfo) {
        // Update fields
        adminInfoInstance.setPhone(adminInfo.getPhone());
        adminInfoInstance.setOrderEmails(adminInfo.getOrderEmails());
        adminInfoInstance.setTiktokLink(adminInfo.getTiktokLink());
        adminInfoInstance.setFacebookLink(adminInfo.getFacebookLink());
        adminInfoInstance.setInstagramLink(adminInfo.getInstagramLink());
        adminInfoInstance.setMainDivImage(adminInfo.getMainDivImage());
        adminInfoInstance.setLogoImage(adminInfo.getLogoImage());

        // Save to DB
        adminInfoInstance = repository.save(adminInfoInstance);
        return adminInfoInstance;
    }
}
