package com.decade.practice.controllers

import com.decade.practice.controllers.converters.ChatIdentifierConverter
import org.springframework.context.annotation.Configuration
import org.springframework.format.FormatterRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfiguration : WebMvcConfigurer {

      override fun addFormatters(registry: FormatterRegistry) {
            registry.addConverter(ChatIdentifierConverter())
      }
      
}