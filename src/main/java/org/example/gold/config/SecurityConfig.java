package org.example.gold.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * إنشاء PasswordEncoder باستخدام BCrypt
     * هذا ضروري لتشفير كلمات المرور بشكل آمن
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * إعداد مستخدم في الذاكرة للمصادقة
     * يمكنك إضافة المزيد من المستخدمين هنا
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        // إنشاء مستخدم admin
        UserDetails admin = User.builder()
                .username("mohamed")
                .password(passwordEncoder.encode("123")) // تشفير كلمة المرور
                .roles("ADMIN", "USER")
                .build();

        // يمكنك إضافة المزيد من المستخدمين
        UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("user123"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, user);
    }

    /**
     * إعداد Security Filter Chain
     * هنا يتم تحديد قواعد الأمان والمصادقة
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // تفعيل CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // تعطيل CSRF (ضروري للـ REST API)
                .csrf(csrf -> csrf.disable())

                // تحديد قواعد الوصول للـ endpoints
                .authorizeHttpRequests(auth -> auth
                        // حماية جميع endpoints التي تبدأ بـ /api/admin
//                        .("/api/admin/**").authenticated()

                        // السماح بالوصول لباقي الـ endpoints بدون مصادقة
                        .anyRequest().permitAll()
                )

                // تفعيل HTTP Basic Authentication
                .httpBasic(Customizer.withDefaults())

                // تعطيل Form Login (اختياري - لأننا نستخدم Basic Auth فقط)
                .formLogin(form -> form.disable());

        return http.build();
    }

    /**
     * إعداد CORS للسماح بالطلبات من أي مصدر
     * في الإنتاج، يُفضل تحديد المصادر المسموح بها بدقة
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // السماح بأي domain (في الإنتاج، حدد domains معينة)
        configuration.setAllowedOriginPatterns(List.of("*"));

        // السماح بجميع HTTP methods
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // السماح بجميع Headers
        configuration.setAllowedHeaders(List.of("*"));

        // السماح بإرسال Credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);

        // السماح بعرض بعض Headers في الاستجابة
        configuration.setExposedHeaders(List.of("Authorization", "Content-Type"));

        // تطبيق إعدادات CORS على جميع الـ endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}