package com.decade.practice.web.converters;

import com.decade.practice.model.domain.embeddable.ChatIdentifier;
import org.springframework.core.convert.converter.Converter;

import java.util.NoSuchElementException;
import java.util.UUID;

public class ChatIdentifierConverter implements Converter<String, ChatIdentifier> {

      @Override
      public ChatIdentifier convert(String source) {
            return extractChatIdentifier(source);
      }

      public static ChatIdentifier extractChatIdentifier(String source) {
            String[] parts = source.split("\\+");
            if (parts.length != 2) {
                  throw new NoSuchElementException();
            }

            UUID first = UUID.fromString(parts[0].trim());
            UUID second = UUID.fromString(parts[1].trim());

            return new ChatIdentifier(first, second);
      }
}