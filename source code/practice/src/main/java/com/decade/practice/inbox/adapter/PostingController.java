package com.decade.practice.inbox.adapter;

import com.decade.practice.inbox.application.MessageAlreadySentException;
import com.decade.practice.inbox.application.query.ChatEventService;
import com.decade.practice.inbox.application.services.*;
import com.decade.practice.inbox.domain.SeenRequest;
import com.decade.practice.inbox.domain.TextRequest;
import com.decade.practice.inbox.dto.FileRequest;
import com.decade.practice.inbox.dto.IconRequest;
import com.decade.practice.inbox.dto.ImageRequest;
import com.decade.practice.inbox.dto.PostingResponse;
import com.decade.practice.inbox.dto.mapper.CommandMappers;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@AllArgsConstructor
public class PostingController {

      private final ChatEventService chatEventService;
      private final CommandMappers commandMappers;
      private final TextPlacement textPlacement;
      private final ImagePlacement imagePlacement;
      private final IconPlacement iconPlacement;
      private final FilePlacement filePlacement;
      private final SeenPlacement seenPlacement;

      @ExceptionHandler(MessageAlreadySentException.class)
      public PostingResponse handle(MessageAlreadySentException e) {
            UUID idempotentKey = e.getIdempotentId();
            log.debug("Concurrent insert encountered for receipt: {}", idempotentKey, e);
            return chatEventService.find(idempotentKey);
      }


      @PutMapping(path = "/chats/{chatId}/texts/{postingId}", consumes = {MediaType.APPLICATION_JSON_VALUE})
      @ResponseStatus(HttpStatus.ACCEPTED)
      public PostingResponse createText(
                @AuthenticationPrincipal(expression = "id") UUID senderId,
                @PathVariable UUID postingId,
                @PathVariable String chatId,
                @RequestBody @Valid TextRequest body) {
            return textPlacement.place(commandMappers.toText(body, postingId, senderId, chatId));
      }

      @PutMapping(path = "/chats/{chatId}/images/{postingId}", consumes = {MediaType.APPLICATION_JSON_VALUE})
      @ResponseStatus(HttpStatus.ACCEPTED)
      public PostingResponse createImage(
                @PathVariable String chatId,
                @AuthenticationPrincipal(expression = "id") UUID senderId,
                @PathVariable UUID postingId,
                @RequestBody @Valid ImageRequest body) {
            return imagePlacement.place(commandMappers.toImage(body, postingId, senderId, chatId));
      }

      @PutMapping(path = "/chats/{chatId}/icons/{postingId}", consumes = {MediaType.APPLICATION_JSON_VALUE})
      @ResponseStatus(HttpStatus.ACCEPTED)
      public PostingResponse createIcon(
                @PathVariable String chatId,
                @AuthenticationPrincipal(expression = "id") UUID senderId,
                @PathVariable UUID postingId,
                @RequestBody @Valid IconRequest body) {
            return iconPlacement.place(commandMappers.toIcon(body, postingId, senderId, chatId));

      }

      @PutMapping(path = "/chats/{chatId}/files/{postingId}", consumes = {MediaType.APPLICATION_JSON_VALUE})
      @ResponseStatus(HttpStatus.ACCEPTED)
      public PostingResponse createFile(
                @PathVariable String chatId,
                @AuthenticationPrincipal(expression = "id") UUID senderId,
                @PathVariable UUID postingId,
                @RequestBody @Valid FileRequest body) {
            return filePlacement.place(commandMappers.toFile(body, postingId, senderId, chatId));
      }

      @PutMapping(path = "/chats/{chatId}/seens/{postingId}", consumes = {MediaType.APPLICATION_JSON_VALUE})
      @ResponseStatus(HttpStatus.ACCEPTED)
      public PostingResponse createSeen(
                @PathVariable String chatId,
                @AuthenticationPrincipal(expression = "id") UUID senderId,
                @PathVariable UUID postingId,
                @RequestBody @Valid SeenRequest body) {
            return seenPlacement.place(commandMappers.toSeen(body, postingId, senderId, chatId));
      }
}