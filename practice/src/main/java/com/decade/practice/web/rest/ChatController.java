package com.decade.practice.web.rest;

import com.decade.practice.database.repository.ChatRepository;
import com.decade.practice.database.repository.EntityHelper;
import com.decade.practice.database.repository.UserRepository;
import com.decade.practice.entities.domain.ChatSnapshot;
import com.decade.practice.entities.domain.embeddable.ChatIdentifier;
import com.decade.practice.entities.domain.entity.Chat;
import com.decade.practice.entities.domain.entity.User;
import com.decade.practice.usecases.core.ChatOperations;
import com.decade.practice.utils.CacheUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/chats")
public class ChatController {

      private final UserRepository userRepository;
      private final ChatRepository chatRepository;
      private final ChatOperations chatOperations;

      public ChatController(
            UserRepository userRepository,
            ChatRepository chatRepository,
            ChatOperations chatOperations
      ) {
            this.userRepository = userRepository;
            this.chatRepository = chatRepository;
            this.chatOperations = chatOperations;
      }

      @GetMapping("/{identifier}")
      public ResponseEntity<ChatSnapshot> get(
            @AuthenticationPrincipal(expression = "name") String username,
            @PathVariable ChatIdentifier identifier,
            @RequestParam(required = false) Integer atVersion
      ) {
            User me = userRepository.getByUsername(username);
            Chat chat = chatOperations.getOrCreateChat(identifier);
            ChatSnapshot snapshot = chatOperations.getSnapshot(
                  chat,
                  me,
                  atVersion != null ? atVersion : me.getSyncContext().getEventVersion()
            );

            return ResponseEntity.ok()
                  .cacheControl(CacheUtils.CACHE_CONTROL)
                  .header("Vary", "Cookie, Authorization")
                  .body(snapshot);
      }

      @GetMapping
      public ResponseEntity<List<ChatSnapshot>> list(
            @AuthenticationPrincipal(expression = "name") String username,
            @RequestParam(required = false) ChatIdentifier startAt,
            @RequestParam int atVersion
      ) {
            Chat chat = startAt == null ? null : EntityHelper.get(chatRepository, startAt);
            User owner = userRepository.getByUsername(username);

            List<Chat> chatList = chatOperations.listChat(owner, atVersion, chat);
            List<ChatSnapshot> snapshotList = new ArrayList<>();
            for (Chat c : chatList) {
                  snapshotList.add(chatOperations.getSnapshot(c, owner, atVersion));
            }
            return ResponseEntity.ok()
                  .cacheControl(CacheUtils.CACHE_CONTROL)
                  .header("Vary", "Cookie, Authorization")
                  .body(snapshotList);
      }
}