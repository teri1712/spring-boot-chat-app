package com.decade.practice.web.rest;

import com.decade.practice.database.repositories.EventRepository;
import com.decade.practice.database.repositories.UserRepository;
import com.decade.practice.entities.domain.embeddable.ChatIdentifier;
import com.decade.practice.entities.domain.embeddable.ImageSpec;
import com.decade.practice.entities.domain.entity.Chat;
import com.decade.practice.entities.domain.entity.ChatEvent;
import com.decade.practice.entities.domain.entity.ImageEvent;
import com.decade.practice.entities.domain.entity.User;
import com.decade.practice.medias.ImageStore;
import com.decade.practice.usecases.ChatEventStore;
import com.decade.practice.usecases.core.ChatOperations;
import com.decade.practice.usecases.core.OnlineStatistic;
import com.decade.practice.utils.CacheUtils;
import com.decade.practice.utils.ChatUtils;
import com.decade.practice.utils.EventUtils;
import com.decade.practice.utils.ImageUtils;
import com.decade.practice.websocket.WsConfiguration;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.List;

import static java.lang.Math.min;

@RestController
@RequestMapping
public class EventController {

        private final ChatEventStore chatEventStore;
        private final SimpMessagingTemplate template;
        private final ChatOperations chatOperations;
        private final EventRepository evenRepo;
        private final OnlineStatistic onlineStat;
        private final UserRepository userRepo;
        private final ImageStore imageStore;

        public EventController(
                ChatEventStore chatEventStore,
                SimpMessagingTemplate template,
                ChatOperations chatOperations,
                EventRepository evenRepo,
                OnlineStatistic onlineStat,
                UserRepository userRepo,
                ImageStore imageStore
        ) {
                this.chatEventStore = chatEventStore;
                this.template = template;
                this.chatOperations = chatOperations;
                this.evenRepo = evenRepo;
                this.onlineStat = onlineStat;
                this.userRepo = userRepo;
                this.imageStore = imageStore;
        }

        private <E extends ChatEvent> E createAndDeliver(
                User sender,
                E event
        ) throws EntityNotFoundException {
                onlineStat.set(sender);
                event.setSender(sender);
                try {
                        Collection<ChatEvent> saved = chatEventStore.save(event);
                        for (ChatEvent it : saved) {
                                template.convertAndSendToUser(
                                        it.getOwner().getUsername(),
                                        WsConfiguration.QUEUE_MESSAGE_DESTINATION,
                                        it
                                );
                        }

                        // Find the event with the same localId as the original event
                        for (ChatEvent it : saved) {
                                if (it.getLocalId().equals(event.getLocalId())) {
                                        @SuppressWarnings("unchecked")
                                        E result = (E) it;
                                        return result;
                                }
                        }
                        throw new EntityNotFoundException("Event not found after save");
                } catch (DataIntegrityViolationException e) {
                        // record already sent
                        @SuppressWarnings("unchecked")
                        E result = (E) evenRepo.getByLocalId(event.getLocalId());
                        return result;
                }
        }

        @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
        @ResponseStatus(HttpStatus.CREATED)
        public ChatEvent createEvent(
                @AuthenticationPrincipal(expression = "name") String username,
                @RequestBody @Valid ChatEvent event) {
                return createAndDeliver(userRepo.getByUsername(username), event);
        }


        @PostMapping(path = "/events", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
        @ResponseStatus(HttpStatus.CREATED)
        public ImageEvent createEvent(
                @AuthenticationPrincipal(expression = "name") String username,
                @RequestPart("event") @Valid ImageEvent event,
                @RequestPart("file") MultipartFile file
        ) throws EntityNotFoundException, IOException {
                int maxWidth = min(event.getImage().getWidth(), ImageSpec.DEFAULT_WIDTH);
                int maxHeight = min(event.getImage().getHeight(), ImageSpec.DEFAULT_HEIGHT);
                BufferedImage image = ImageUtils.crop(file.getInputStream(), maxWidth, maxHeight);

                event.setImage(imageStore.save(image));
                try {
                        return createAndDeliver(userRepo.getByUsername(username), event);
                } catch (Exception e) {
                        imageStore.remove(URI.create(event.getImage().getUri()));
                        throw e;
                }
        }

        @GetMapping("/chats/{chatIdentifier}/events")
        public ResponseEntity<List<ChatEvent>> listEvents(
                @AuthenticationPrincipal(expression = "name") String username,
                @PathVariable @Validated ChatIdentifier chatIdentifier,
                @RequestParam int atVersion
        ) throws EntityNotFoundException {
                Chat chat = chatOperations.getOrCreateChat(chatIdentifier);
                User user = ChatUtils.inspectOwner(chat, username);
                List<ChatEvent> events = evenRepo.findByOwnerAndChatAndEventVersionLessThanEqual(
                        user, chat, atVersion, EventUtils.EVENT_VERSION_LESS_THAN_EQUAL);

                return ResponseEntity.ok()
                        .header("Vary", "Cookie, Authorization")
                        .cacheControl(CacheUtils.CACHE_CONTROL)
                        .body(events);
        }

        @GetMapping("/users/me/events")
        public ResponseEntity<List<ChatEvent>> listEvents(
                @AuthenticationPrincipal(expression = "name") String username,
                @RequestParam int atVersion
        ) throws EntityNotFoundException {
                User owner = userRepo.getByUsername(username);
                List<ChatEvent> eventList = evenRepo.findByOwnerAndEventVersionLessThanEqual(
                        owner, atVersion, EventUtils.EVENT_VERSION_LESS_THAN_EQUAL);

                return ResponseEntity.ok()
                        .header("Vary", "Cookie, Authorization")
                        .cacheControl(CacheUtils.CACHE_CONTROL)
                        .body(eventList);
        }
}