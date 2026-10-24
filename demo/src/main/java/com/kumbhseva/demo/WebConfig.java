package com.kumbhseva.demo;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

import java.io.File;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String userDir = System.getProperty("user.dir");
        File uploadFolder = new File(userDir, "uploads");
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs();
        }
        String uploadUri = uploadFolder.toURI().toString();

        File parentUploadFolder = new File(new File(userDir).getParentFile(), "uploads");
        String parentUploadUri = parentUploadFolder.toURI().toString();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadUri, parentUploadUri, "file:C:/uploads/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}