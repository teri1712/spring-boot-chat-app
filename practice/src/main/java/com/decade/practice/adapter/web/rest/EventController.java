package com.decade.practice.adapter.web.rest;

import com.decade.practice.application.usecases.*;
import com.decade.practice.domain.embeddables.ChatIdentifier;
import com.decade.practice.domain.embeddables.ImageSpec;
import com.decade.practice.domain.entities.*;
import com.decade.practice.domain.repositories.UserRepository;
import com.decade.practice.utils.ChatUtils;
import com.decade.practice.utils.ImageUtils;
import com.decade.practice.utils.WebCacheUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

import static java.lang.Math.min;

@RestController
@RequestMapping
public class EventController {

        private final EventService eventService;
        private final DeliveryService deliveryService;
        private final ChatService chatService;
        private final UserPresenceService presenceService;
        private final UserRepository userRepo;
        private final ImageStore imageStore;
        private final MediaStore mediaStore;

        public EventController(
                EventService eventService,
                DeliveryService deliveryService,
                ChatService chatService,
                UserPresenceService presenceService,
                UserRepository userRepo,
                ImageStore imageStore,
                MediaStore mediaStore) {
                this.eventService = eventService;
                this.deliveryService = deliveryService;
                this.chatService = chatService;
                this.presenceService = presenceService;
                this.userRepo = userRepo;
                this.imageStore = imageStore;
                this.mediaStore = mediaStore;
        }


        @PostMapping(path = "/events", consumes = {MediaType.APPLICATION_JSON_VALUE})
        @ResponseStatus(HttpStatus.CREATED)
        public ChatEvent createEvent(
                Principal principal,
                @RequestBody @Valid ChatEvent event) {
                User user = userRepo.findByUsername(principal.getName());
                presenceService.set(user);
                return deliveryService.createAndSend(user, event);
        }


        @PostMapping(path = "/events", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, headers = "X-File-Type=image")
        @ResponseStatus(HttpStatus.CREATED)
        public ImageEvent createEvent(
                Principal principal,
                @RequestPart("event") @Valid ImageEvent event,
                @RequestPart("file") MultipartFile file
        ) throws EntityNotFoundException, IOException {
                int maxWidth = min(event.getImage().getWidth(), ImageSpec.DEFAULT_WIDTH);
                int maxHeight = min(event.getImage().getHeight(), ImageSpec.DEFAULT_HEIGHT);
                BufferedImage image = ImageUtils.crop(file.getInputStream(), maxWidth, maxHeight);

                event.setImage(imageStore.save(image));
                try {
                        User user = userRepo.findByUsername(principal.getName());
                        presenceService.set(user);
                        return deliveryService.createAndSend(user, event);
                } catch (Exception e) {
                        imageStore.remove(URI.create(event.getImage().getUri()));
                        throw e;
                }
        }

        @PostMapping(path = "/events", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, headers = "X-File-Type=file")
        @ResponseStatus(HttpStatus.CREATED)
        public FileEvent createEvent(
                Principal principal,
                @RequestPart("event") @Valid FileEvent event,
                @RequestPart("file") MultipartFile file
        ) throws EntityNotFoundException, IOException {
                URI uri = mediaStore.save(file.getResource(), UUID.randomUUID().toString());
                event.setMediaUrl(uri.toString());
                try {
                        User user = userRepo.findByUsername(principal.getName());
                        presenceService.set(user);
                        return deliveryService.createAndSend(user, event);
                } catch (Exception e) {
                        mediaStore.remove(uri);
                        throw e;
                }
        }

        @GetMapping("/chats/{chatIdentifier}/events")
        public ResponseEntity<List<ChatEvent>> listEvents(
                Principal principal,
                @PathVariable @Validated ChatIdentifier chatIdentifier,
                @RequestParam int atVersion
        ) throws EntityNotFoundException {
                Chat chat = chatService.getOrCreateChat(chatIdentifier);
                User user = ChatUtils.inspectOwner(chat, principal.getName());
                List<ChatEvent> events = eventService.findByOwnerAndChatAndEventVersionLessThanEqual(user, chat, atVersion);

                return ResponseEntity.ok()
                        .header("Vary", "Cookie", "Authorization")
                        .cacheControl(WebCacheUtils.ONE_MONTHS)
                        .body(events);
        }

        @GetMapping("/users/me/events")
        public ResponseEntity<List<ChatEvent>> listEvents(
                Principal principal,
                @RequestParam int atVersion
        ) throws EntityNotFoundException {
                User owner = userRepo.findByUsername(principal.getName());
                List<ChatEvent> eventList = eventService.findByOwnerAndEventVersionLessThanEqual(owner, atVersion);

                return ResponseEntity.ok()
                        .header("Vary", "Cookie", "Authorization")
                        .cacheControl(WebCacheUtils.ONE_MONTHS)
                        .body(eventList);
        }
}