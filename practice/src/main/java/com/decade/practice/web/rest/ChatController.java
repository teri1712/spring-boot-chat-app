package com.decade.practice.web.rest;

import com.decade.practice.data.repositories.ChatRepository;
import com.decade.practice.data.repositories.EntityHelper;
import com.decade.practice.data.repositories.ThemeRepository;
import com.decade.practice.data.repositories.UserRepository;
import com.decade.practice.models.domain.ChatSnapshot;
import com.decade.practice.models.domain.embeddable.ChatIdentifier;
import com.decade.practice.models.domain.embeddable.Preference;
import com.decade.practice.models.domain.entity.Chat;
import com.decade.practice.models.domain.entity.PreferenceEvent;
import com.decade.practice.models.domain.entity.Theme;
import com.decade.practice.models.domain.entity.User;
import com.decade.practice.usecases.ChatOperations;
import com.decade.practice.usecases.EventOperations;
import com.decade.practice.utils.CacheUtils;
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
        private final ChatOperations chatOperations;
        private final ThemeRepository themeRepository;
        private final EventOperations eventOperations;

        public ChatController(
                UserRepository userRepository,
                ChatRepository chatRepository,
                ChatOperations chatOperations,
                ThemeRepository themeRepository,
                EventOperations eventOperations
        ) {
                this.userRepository = userRepository;
                this.chatRepository = chatRepository;
                this.chatOperations = chatOperations;
                this.themeRepository = themeRepository;
                this.eventOperations = eventOperations;
        }

        @GetMapping("/{identifier}")
        public ResponseEntity<ChatSnapshot> get(
                Principal principal,
                @PathVariable ChatIdentifier identifier,
                @RequestParam(required = false) Integer atVersion
        ) {
                User me = userRepository.getByUsername(principal.getName());
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

        @GetMapping("/partners/{partnerId}")
        public ResponseEntity<ChatSnapshot> get(
                Principal principal,
                @PathVariable UUID partnerId,
                @RequestParam(required = false) Integer atVersion
        ) {
                User me = userRepository.getByUsername(principal.getName());
                ChatIdentifier identifier = ChatIdentifier.from(partnerId, me.getId());
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
        public Preference updatePreferences(
                Principal principal,
                @PathVariable ChatIdentifier identifier,
                @RequestBody Preference preference) {

                User me = userRepository.getByUsername(principal.getName());

                Theme theme = preference.getTheme();
                if (theme != null) {
                        preference.setTheme(themeRepository.findById(theme.getId()).orElseThrow());
                }
                PreferenceEvent event = new PreferenceEvent();
                event.setChatIdentifier(identifier);
                event.setSender(me);
                event.setPreference(preference);
                eventOperations.createAndSend(me, event);

                return preference;
        }

        @GetMapping("/themes")
        public ResponseEntity<List<Theme>> getThemes() {
                return ResponseEntity.ok().cacheControl(
                                CacheUtils.ONE_MONTHS.cachePublic())
                        .body(themeRepository.findAll());
        }

        @GetMapping
        public ResponseEntity<List<ChatSnapshot>> list(
                Principal principal,
                @RequestParam(required = false) ChatIdentifier startAt,
                @RequestParam int atVersion
        ) {
                Chat chat = startAt == null ? null : EntityHelper.get(chatRepository, startAt);
                User owner = userRepository.getByUsername(principal.getName());

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