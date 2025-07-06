package com.decade.practice.web;

import com.decade.practice.web.converters.ChatIdentifierConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

      @Override
      public void addFormatters(FormatterRegistry registry) {
            registry.addConverter(new ChatIdentifierConverter());
      }
}