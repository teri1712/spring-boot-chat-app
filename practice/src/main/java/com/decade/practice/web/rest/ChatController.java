package com.decade.practice.web.rest;

import com.decade.practice.core.ChatOperations;
import com.decade.practice.database.repository.ChatRepository;
import com.decade.practice.database.repository.EntityHelper;
import com.decade.practice.database.repository.UserRepository;
import com.decade.practice.model.domain.ChatSnapshot;
import com.decade.practice.model.domain.embeddable.ChatIdentifier;
import com.decade.practice.model.domain.entity.Chat;
import com.decade.practice.model.domain.entity.User;
import com.decade.practice.utils.CacheUtils;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatController {

      private final UserRepository userRepo;
      private final ChatRepository chatRepo;
      private final ChatOperations chatOperations;

      public ChatController(
            UserRepository userRepo,
            ChatRepository chatRepo,
            ChatOperations chatOperations
      ) {
            this.userRepo = userRepo;
            this.chatRepo = chatRepo;
            this.chatOperations = chatOperations;
      }

      @GetMapping("/snapshot")
      public ResponseEntity<ChatSnapshot> get(
            @AuthenticationPrincipal(expression = "name") String username,
            @RequestParam @Validated ChatIdentifier identifier,
            @RequestParam(required = false) Integer atVersion
      ) {
            CacheControl cacheControl = CacheUtils.DEFAULT_CACHE_CONTROL;
            User me = userRepo.getByUsername(username);
            Chat chat = chatOperations.getOrCreateChat(identifier);
            ChatSnapshot snapshot = chatOperations.getSnapshot(
                  chat,
                  me,
                  atVersion != null ? atVersion : me.getSyncContext().getEventVersion()
            );

            return ResponseEntity.ok()
                  .cacheControl(cacheControl)
                  .header("Vary", "Cookie, Authorization")
                  .body(snapshot);
      }

      @GetMapping
      public ResponseEntity<List<ChatSnapshot>> list(
            @AuthenticationPrincipal(expression = "name") String username,
            @RequestParam(required = false) @Validated ChatIdentifier startAt,
            @RequestParam int atVersion
      ) {
            Chat chat = startAt == null ? null : EntityHelper.get(chatRepo, startAt);
            User owner = userRepo.getByUsername(username);

            CacheControl cacheControl = CacheUtils.DEFAULT_CACHE_CONTROL;
            List<Chat> chatList = chatOperations.listChat(owner, atVersion, chat);
            List<ChatSnapshot> snapshotList = new ArrayList<>();
            for (Chat c : chatList) {
                  snapshotList.add(chatOperations.getSnapshot(c, owner, atVersion));
            }
            return ResponseEntity.ok()
                  .cacheControl(cacheControl)
                  .header("Vary", "Cookie, Authorization")
                  .body(snapshotList);
      }
}