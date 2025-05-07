package com.decade.practice.controllers.converters

import com.decade.practice.model.domain.embeddable.ChatIdentifier
import org.springframework.core.convert.converter.Converter
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