package com.decade.practice.web;

import com.decade.practice.web.converters.ChatIdentifierConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.ContentVersionStrategy;
import org.springframework.web.servlet.resource.VersionResourceResolver;

import java.util.concurrent.TimeUnit;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebConfiguration implements WebMvcConfigurer {

        @Override
        public void addFormatters(FormatterRegistry registry) {
                registry.addConverter(new ChatIdentifierConverter());
        }

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
                // === 1. Handler for VERSIONED resources (your custom CSS/JS) ===
                // These are files you expect to change. Spring will add a content hash to the URL.
                // e.g., /css/main.css -> /css/main-a1b2c3d4e5f.css
                VersionResourceResolver versionResolver = new VersionResourceResolver()
                        .addVersionStrategy(new ContentVersionStrategy(), "**/*.css", "**/*.js");

                registry.addResourceHandler("/*.css", "/*.js")
                        .addResourceLocations("classpath:/static/")
                        .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS))
                        .resourceChain(true)
                        .addResolver(versionResolver);


                // === 2. Handler for NON-VERSIONED resources (e.g., libraries, images) ===
                // These are files that are less likely to change or are third-party.
                // They will be served directly without a version hash.
                registry.addResourceHandler("/theme/**")
                        .addResourceLocations(
                                "classpath:/static/"
                        )
                        .setCacheControl(CacheControl.maxAge(7, TimeUnit.DAYS));
        }
}