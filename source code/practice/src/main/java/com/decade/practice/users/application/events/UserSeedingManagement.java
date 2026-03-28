package com.decade.practice.users.application.events;

import com.decade.practice.users.application.ports.out.UserRepository;
import com.decade.practice.users.domain.events.UserCreated;
import com.decade.practice.users.utils.GenderUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserSeedingManagement {

      private final UserRepository users;
      private final ApplicationEventPublisher publisher;

      @EventListener(ApplicationReadyEvent.class)
      @Transactional
      public void onApplicationReady() {
            UUID luffy = UUID.fromString("00000000-0000-0000-0000-000000000001");
            UUID nami = UUID.fromString("00000000-0000-0000-0000-000000000003");
            UUID chopper = UUID.fromString("00000000-0000-0000-0000-000000000004");
            UUID zoro = UUID.fromString("00000000-0000-0000-0000-000000000005");

            users.findAllById(List.of(luffy, nami, chopper, zoro)).forEach(user -> {
                  publisher.publishEvent(new UserCreated(user.getId(), user.getUsername(), user.getName(), GenderUtils.inspect(user.getGender()), user.getDob(), user.getAvatar()));
            });
      }
}
