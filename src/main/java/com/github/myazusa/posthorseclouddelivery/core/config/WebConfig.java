package com.github.myazusa.posthorseclouddelivery.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${app.resource.location.upload.origin}")
    private String uploadResourceLocation;

    @Value("${app.resource.location.upload.mapping}")
    private String uploadResourceLocationHttpMapping;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(uploadResourceLocationHttpMapping)
                .addResourceLocations(uploadResourceLocation)
                .setCacheControl(CacheControl.maxAge(Duration.ofDays(30)))
                .resourceChain(true);
    }
}
