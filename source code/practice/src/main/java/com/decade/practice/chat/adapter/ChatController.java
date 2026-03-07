package com.decade.practice.chat.adapter;

import com.decade.practice.chat.application.ports.in.ChatService;
import com.decade.practice.chat.application.ports.in.CreateGroupChatCommand;
import com.decade.practice.chat.dto.ChatResponse;
import com.decade.practice.chat.dto.DirectChatResponse;
import com.decade.practice.engagement.api.ChatIdentifierUniqueException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@AllArgsConstructor
public class ChatController {

      private final ChatService chatService;

      @ExceptionHandler(ChatIdentifierUniqueException.class)
      @ResponseStatus(HttpStatus.ACCEPTED)
      public ChatResponse handle(
                @AuthenticationPrincipal(expression = "id") UUID userId,
                ChatIdentifierUniqueException e) {
            String chatId = e.getId();
            log.debug("Concurrent insert encountered for chat: {}", chatId, e);
            return chatService.find(chatId, userId);
      }

      @GetMapping("/chats/{chatId}")
      public ChatResponse getChat(
                @AuthenticationPrincipal(expression = "id") UUID userId,
                @PathVariable String chatId
      ) {
            return chatService.find(chatId, userId);
      }

      @PostMapping("/chats")
      public ResponseEntity<ChatResponse> getChat(
                @AuthenticationPrincipal(expression = "id") UUID userId,
                @RequestParam UUID partnerId
      ) {
            DirectChatResponse directChat = chatService.getDirect(userId, partnerId);
            HttpStatus status = directChat.newly() ? HttpStatus.CREATED : HttpStatus.OK;
            return ResponseEntity.status(status).body(directChat.details());
      }

      @PostMapping("/groups")
      @ResponseStatus(HttpStatus.CREATED)
      public ChatResponse createGroupChat(
                @AuthenticationPrincipal(expression = "id") UUID userId,
                @RequestParam UUID partnerId,
                @RequestParam String roomName
      ) {
            return chatService.createGroup(new CreateGroupChatCommand(userId, partnerId, roomName));
      }


}