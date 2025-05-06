package com.decade.practice.controllers

import com.decade.practice.model.domain.embeddable.ChatIdentifier
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.format.FormatterRegistry
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.util.*

fun String.extractChatIdentifier(): ChatIdentifier {
      val parts = split("+")
      if (parts.size != 2)
            throw NoSuchElementException()
      val (first, second) = parts.map { UUID.fromString(it.trim()) }
      if (first > second)
            throw NoSuchElementException()
      return ChatIdentifier(first, second)
}

class ChatIdentifierConverter : Converter<String, ChatIdentifier> {
      override fun convert(source: String): ChatIdentifier = source.extractChatIdentifier()
}


@Configuration
class WebConfiguration : WebMvcConfigurer {
      override fun addFormatters(registry: FormatterRegistry) {
            registry.addConverter(ChatIdentifierConverter())
      }

      override fun addCorsMappings(registry: CorsRegistry) {
            super.addCorsMappings(registry)
      }
}