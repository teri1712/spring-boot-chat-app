package com.decade.practice.web.rest;

import com.decade.practice.data.repositories.EventRepository;
import com.decade.practice.data.repositories.UserRepository;
import com.decade.practice.media.ImageStore;
import com.decade.practice.media.MediaStore;
import com.decade.practice.model.domain.embeddable.ChatIdentifier;
import com.decade.practice.model.domain.embeddable.ImageSpec;
import com.decade.practice.model.domain.entity.*;
import com.decade.practice.presence.UserPresenceService;
import com.decade.practice.usecases.ChatOperations;
import com.decade.practice.usecases.EventOperations;
import com.decade.practice.utils.CacheUtils;
import com.decade.practice.utils.ChatUtils;
import com.decade.practice.utils.EventUtils;
import com.decade.practice.utils.ImageUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import static java.lang.Math.min;

@RestController
@RequestMapping
public class EventController {

        private final EventOperations eventOperations;
        private final ChatOperations chatOperations;
        private final EventRepository evenRepo;
        private final UserPresenceService presenceService;
        private final UserRepository userRepo;
        private final ImageStore imageStore;
        private final MediaStore mediaStore;

        public EventController(
                EventOperations eventOperations,
                ChatOperations chatOperations,
                EventRepository evenRepo,
                UserPresenceService presenceService,
                UserRepository userRepo,
                ImageStore imageStore,
                MediaStore mediaStore) {
                this.eventOperations = eventOperations;
                this.chatOperations = chatOperations;
                this.evenRepo = evenRepo;
                this.presenceService = presenceService;
                this.userRepo = userRepo;
                this.imageStore = imageStore;
                this.mediaStore = mediaStore;
        }


        @PostMapping(path = "/events", consumes = {MediaType.APPLICATION_JSON_VALUE})
        @ResponseStatus(HttpStatus.CREATED)
        public ChatEvent createEvent(
                @AuthenticationPrincipal(expression = "username") String username,
                @RequestBody @Valid ChatEvent event) {
                User user = userRepo.getByUsername(username);
                presenceService.set(user);
                return eventOperations.createAndSend(user, event);
        }


        @PostMapping(path = "/events", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, headers = "X-File-Type=image")
        @ResponseStatus(HttpStatus.CREATED)
        public ImageEvent createEvent(
                @AuthenticationPrincipal(expression = "username") String username,
                @RequestPart("event") @Valid ImageEvent event,
                @RequestPart("file") MultipartFile file
        ) throws EntityNotFoundException, IOException {
                int maxWidth = min(event.getImage().getWidth(), ImageSpec.DEFAULT_WIDTH);
                int maxHeight = min(event.getImage().getHeight(), ImageSpec.DEFAULT_HEIGHT);
                BufferedImage image = ImageUtils.crop(file.getInputStream(), maxWidth, maxHeight);

                event.setImage(imageStore.save(image));
                try {
                        User user = userRepo.getByUsername(username);
                        presenceService.set(user);
                        return eventOperations.createAndSend(user, event);
                } catch (Exception e) {
                        imageStore.remove(URI.create(event.getImage().getUri()));
                        throw e;
                }
        }

        @PostMapping(path = "/events", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, headers = "X-File-Type=file")
        @ResponseStatus(HttpStatus.CREATED)
        public FileEvent createEvent(
                @AuthenticationPrincipal(expression = "username") String username,
                @RequestPart("event") @Valid FileEvent event,
                @RequestPart("file") MultipartFile file
        ) throws EntityNotFoundException, IOException {
                URI uri = mediaStore.save(file.getResource(), UUID.randomUUID().toString());
                event.setMediaUrl(uri.toString());
                try {
                        User user = userRepo.getByUsername(username);
                        presenceService.set(user);
                        return eventOperations.createAndSend(user, event);
                } catch (Exception e) {
                        mediaStore.remove(uri);
                        throw e;
                }
        }

        @GetMapping("/chats/{chatIdentifier}/events")
        public ResponseEntity<List<ChatEvent>> listEvents(
                @AuthenticationPrincipal(expression = "username") String username,
                @PathVariable @Validated ChatIdentifier chatIdentifier,
                @RequestParam int atVersion
        ) throws EntityNotFoundException {
                Chat chat = chatOperations.getOrCreateChat(chatIdentifier);
                User user = ChatUtils.inspectOwner(chat, username);
                List<ChatEvent> events = evenRepo.findByOwnerAndChatAndEventVersionLessThanEqual(
                        user, chat, atVersion, EventUtils.EVENT_VERSION_LESS_THAN_EQUAL);

                return ResponseEntity.ok()
                        .header("Vary", "Cookie", "Authorization")
                        .cacheControl(CacheUtils.ONE_MONTHS)
                        .body(events);
        }

        @GetMapping("/users/me/events")
        public ResponseEntity<List<ChatEvent>> listEvents(
                @AuthenticationPrincipal(expression = "username") String username,
                @RequestParam int atVersion
        ) throws EntityNotFoundException {
                User owner = userRepo.getByUsername(username);
                List<ChatEvent> eventList = evenRepo.findByOwnerAndEventVersionLessThanEqual(
                        owner, atVersion, EventUtils.EVENT_VERSION_LESS_THAN_EQUAL);

                return ResponseEntity.ok()
                        .header("Vary", "Cookie", "Authorization")
                        .cacheControl(CacheUtils.ONE_MONTHS)
                        .body(eventList);
        }
}