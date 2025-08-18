package com.decade.practice.web.rest;

import com.decade.practice.data.repositories.ChatRepository;
import com.decade.practice.data.repositories.EntityHelper;
import com.decade.practice.data.repositories.ThemeRepository;
import com.decade.practice.data.repositories.UserRepository;
import com.decade.practice.model.domain.ChatSnapshot;
import com.decade.practice.model.domain.embeddable.ChatIdentifier;
import com.decade.practice.model.domain.embeddable.Preference;
import com.decade.practice.model.domain.entity.Chat;
import com.decade.practice.model.domain.entity.Theme;
import com.decade.practice.model.domain.entity.User;
import com.decade.practice.usecases.ChatOperations;
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
        private final ThemeRepository themeRepository;

        public ChatController(
                UserRepository userRepository,
                ChatRepository chatRepository,
                ChatOperations chatOperations,
                ThemeRepository themeRepository
        ) {
                this.userRepository = userRepository;
                this.chatRepository = chatRepository;
                this.chatOperations = chatOperations;
                this.themeRepository = themeRepository;
        }

        @GetMapping("/{identifier}")
        public ResponseEntity<ChatSnapshot> get(
                @AuthenticationPrincipal(expression = "username") String username,
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
                        .cacheControl(CacheUtils.ONE_MONTHS)
                        .header("Vary", "Cookie", "Authorization")
                        .body(snapshot);
        }

        @PatchMapping("/{identifier}/preference")
        public com.decade.practice.model.local.Chat updatePreferences(
                @AuthenticationPrincipal(expression = "username") String username,
                @PathVariable ChatIdentifier identifier,
                @RequestBody Preference preference) {

                User me = userRepository.getByUsername(username);
                Chat chat = chatOperations.getOrCreateChat(identifier);

                preference.setTheme(themeRepository.findById(preference.getTheme().getId()).orElseThrow());
                chat.setPreference(preference);
                chatRepository.save(chat);

                return new com.decade.practice.model.local.Chat(chat, me);
        }

        @GetMapping("/themes")
        public ResponseEntity<List<Theme>> getThemes() {
                return ResponseEntity.ok().cacheControl(
                                CacheUtils.ONE_MONTHS.cachePublic())
                        .body(themeRepository.findAll());
        }

        @GetMapping
        public ResponseEntity<List<ChatSnapshot>> list(
                @AuthenticationPrincipal(expression = "username") String username,
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
                        .cacheControl(CacheUtils.ONE_MONTHS)
                        .header("Vary", "Cookie", "Authorization")
                        .body(snapshotList);
        }
}