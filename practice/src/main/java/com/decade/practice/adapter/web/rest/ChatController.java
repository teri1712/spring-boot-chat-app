package com.decade.practice.adapter.web.rest;

import com.decade.practice.application.usecases.ChatService;
import com.decade.practice.application.usecases.DeliveryService;
import com.decade.practice.domain.ChatSnapshot;
import com.decade.practice.domain.embeddables.ChatIdentifier;
import com.decade.practice.domain.embeddables.Preference;
import com.decade.practice.domain.entities.Chat;
import com.decade.practice.domain.entities.PreferenceEvent;
import com.decade.practice.domain.entities.Theme;
import com.decade.practice.domain.entities.User;
import com.decade.practice.domain.repositories.ChatRepository;
import com.decade.practice.domain.repositories.ThemeRepository;
import com.decade.practice.domain.repositories.UserRepository;
import com.decade.practice.utils.WebCacheUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/chats")
public class ChatController {

        private final UserRepository userRepository;
        private final ChatRepository chatRepository;
        private final ChatService chatService;
        private final ThemeRepository themeRepository;
        private final DeliveryService deliveryService;

        public ChatController(
                UserRepository userRepository,
                ChatRepository chatRepository,
                ChatService chatService,
                ThemeRepository themeRepository,
                DeliveryService deliveryService
        ) {
                this.userRepository = userRepository;
                this.chatRepository = chatRepository;
                this.chatService = chatService;
                this.themeRepository = themeRepository;
                this.deliveryService = deliveryService;
        }

        @GetMapping("/{identifier}")
        public ResponseEntity<ChatSnapshot> get(
                Principal principal,
                @PathVariable ChatIdentifier identifier,
                @RequestParam(required = false) Integer atVersion
        ) {
                User me = userRepository.findByUsername(principal.getName());
                Chat chat = chatService.getOrCreateChat(identifier);
                ChatSnapshot snapshot = chatService.getSnapshot(
                        chat,
                        me,
                        atVersion != null ? atVersion : me.getSyncContext().getEventVersion()
                );

                return ResponseEntity.ok()
                        .cacheControl(WebCacheUtils.ONE_MONTHS)
                        .header("Vary", "Cookie", "Authorization")
                        .body(snapshot);
        }

        @GetMapping("/partners/{partnerId}")
        public ResponseEntity<ChatSnapshot> get(
                Principal principal,
                @PathVariable UUID partnerId,
                @RequestParam(required = false) Integer atVersion
        ) {
                User me = userRepository.findByUsername(principal.getName());
                ChatIdentifier identifier = ChatIdentifier.from(partnerId, me.getId());
                Chat chat = chatService.getOrCreateChat(identifier);
                ChatSnapshot snapshot = chatService.getSnapshot(
                        chat,
                        me,
                        atVersion != null ? atVersion : me.getSyncContext().getEventVersion()
                );

                return ResponseEntity.ok()
                        .cacheControl(WebCacheUtils.ONE_MONTHS)
                        .header("Vary", "Cookie", "Authorization")
                        .body(snapshot);
        }

        @PatchMapping("/{identifier}/preference")
        public Preference updatePreferences(
                Principal principal,
                @PathVariable ChatIdentifier identifier,
                @RequestBody Preference preference) {

                User me = userRepository.findByUsername(principal.getName());

                Theme theme = preference.getTheme();
                if (theme != null) {
                        preference.setTheme(themeRepository.findById(theme.getId()).orElseThrow());
                }
                PreferenceEvent event = new PreferenceEvent();
                event.setChatIdentifier(identifier);
                event.setSender(me);
                event.setPreference(preference);
                deliveryService.createAndSend(me, event);

                return preference;
        }

        @GetMapping("/themes")
        public ResponseEntity<List<Theme>> getThemes() {
                return ResponseEntity.ok().cacheControl(
                                WebCacheUtils.ONE_MONTHS.cachePublic())
                        .body(themeRepository.findAll());
        }

        @GetMapping
        public ResponseEntity<List<ChatSnapshot>> list(
                Principal principal,
                @RequestParam(required = false) ChatIdentifier startAt,
                @RequestParam int atVersion
        ) {
                Chat chat = startAt == null ? null : chatRepository.findById(startAt).get();
                User owner = userRepository.findByUsername(principal.getName());

                List<Chat> chatList = chatService.listChat(owner, atVersion, chat);
                List<ChatSnapshot> snapshotList = new ArrayList<>();
                for (Chat c : chatList) {
                        snapshotList.add(chatService.getSnapshot(c, owner, atVersion));
                }
                return ResponseEntity.ok()
                        .cacheControl(WebCacheUtils.ONE_MONTHS)
                        .header("Vary", "Cookie", "Authorization")
                        .body(snapshotList);
        }
}