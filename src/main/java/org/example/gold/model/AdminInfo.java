package org.example.gold.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Stores admin contact info, social links, and main images (logo & main div image).
 */
@Entity
@Table(name = "admin_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminInfo {

    /** Primary key */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Admin phone number */
    @Column(length = 20)
    private String phone;

    /** Email addresses for receiving orders (comma-separated) */
    @Column(columnDefinition = "TEXT")
    private String orderEmails;

    /** TikTok profile link */
    @Column(length = 200)
    private String tiktokLink;

    /** Facebook profile/page link */
    @Column(length = 200)
    private String facebookLink;

    /** Instagram profile link */
    @Column(length = 200)
    private String instagramLink;

    /** URL or path to the main div image */
    @Column(length = 500)
    private String mainDivImage;

    /** URL or path to the logo image */
    @Column(length = 500)
    private String logoImage;
}
