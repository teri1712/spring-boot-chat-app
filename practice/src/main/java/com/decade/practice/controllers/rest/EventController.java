package com.decade.practice.controllers.rest;

import com.decade.practice.core.ChatOperations;
import com.decade.practice.core.OnlineStatistic;
import com.decade.practice.database.repository.EventRepository;
import com.decade.practice.database.repository.UserRepository;
import com.decade.practice.database.transaction.ChatEventStore;
import com.decade.practice.image.ImageStore;
import com.decade.practice.model.domain.embeddable.ChatIdentifier;
import com.decade.practice.model.domain.embeddable.ImageSpec;
import com.decade.practice.model.domain.entity.*;
import com.decade.practice.utils.CacheUtils;
import com.decade.practice.utils.ChatUtils;
import com.decade.practice.utils.EventUtils;
import com.decade.practice.utils.ImageUtils;
import com.decade.practice.websocket.WsConfiguration;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/message")
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

      private <E extends ChatEvent> E pushEvent(
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
                              WsConfiguration.MQ_DESTINATION,
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

      @PostMapping("/seen")
      @ResponseStatus(HttpStatus.CREATED)
      public SeenEvent pushEvent(
            @AuthenticationPrincipal(expression = "name") String username,
            @RequestBody @Valid SeenEvent record
      ) throws EntityNotFoundException {
            return pushEvent(userRepo.getByUsername(username), record);
      }

      @PostMapping("/text")
      @ResponseStatus(HttpStatus.CREATED)
      public TextEvent pushEvent(
            @AuthenticationPrincipal(expression = "name") String username,
            @RequestBody @Valid TextEvent event
      ) throws EntityNotFoundException {
            return pushEvent(userRepo.getByUsername(username), event);
      }

      @PostMapping("/icon")
      @ResponseStatus(HttpStatus.CREATED)
      public IconEvent pushEvent(
            @AuthenticationPrincipal(expression = "name") String username,
            @RequestBody @Valid IconEvent event
      ) throws EntityNotFoundException {
            return pushEvent(userRepo.getByUsername(username), event);
      }

      @PostMapping("/image")
      @ResponseStatus(HttpStatus.CREATED)
      public ImageEvent pushEvent(
            @AuthenticationPrincipal(expression = "name") String username,
            @RequestPart("event") @Valid ImageEvent event,
            @RequestPart("file") MultipartFile file
      ) throws EntityNotFoundException, IOException {
            int maxWidth = min(event.getImage().getWidth(), ImageSpec.DEFAULT_WIDTH);
            int maxHeight = min(event.getImage().getHeight(), ImageSpec.DEFAULT_HEIGHT);
            BufferedImage image = ImageUtils.crop(file.getInputStream(), maxWidth, maxHeight);

            event.setImage(imageStore.save(image));
            try {
                  return pushEvent(userRepo.getByUsername(username), event);
            } catch (Exception e) {
                  imageStore.remove(URI.create(event.getImage().getUri()));
                  throw e;
            }
      }

      @GetMapping("/chat")
      public ResponseEntity<List<ChatEvent>> pullEvents(
            @AuthenticationPrincipal(expression = "name") String username,
            @RequestParam @Validated ChatIdentifier identifier,
            @RequestParam int atVersion
      ) throws EntityNotFoundException {
            Chat chat = chatOperations.getOrCreateChat(identifier);
            User user = ChatUtils.inspectOwner(chat, username);
            CacheControl cacheControl = CacheUtils.DEFAULT_CACHE_CONTROL;
            List<ChatEvent> events = evenRepo.findByOwnerAndChatAndEventVersionLessThanEqual(
                  user, chat, atVersion, EventUtils.pageEvent);

            return ResponseEntity.ok()
                  .header("Vary", "Cookie, Authorization")
                  .cacheControl(cacheControl)
                  .body(events);
      }

      @GetMapping
      public ResponseEntity<List<ChatEvent>> pullEvents(
            @AuthenticationPrincipal(expression = "name") String username,
            @RequestParam int atVersion
      ) throws EntityNotFoundException {
            User owner = userRepo.getByUsername(username);
            CacheControl cacheControl = CacheUtils.DEFAULT_CACHE_CONTROL;
            List<ChatEvent> eventList = evenRepo.findByOwnerAndEventVersionLessThanEqual(
                  owner, atVersion, EventUtils.pageEvent);

            return ResponseEntity.ok()
                  .header("Vary", "Cookie, Authorization")
                  .cacheControl(cacheControl)
                  .body(eventList);
      }
}