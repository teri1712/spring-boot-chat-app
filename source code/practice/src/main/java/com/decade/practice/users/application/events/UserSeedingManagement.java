package com.decade.practice.users.application.events;

import com.decade.practice.users.application.ports.out.UserRepository;
import com.decade.practice.users.domain.events.UserCreated;
import com.decade.practice.users.utils.GenderUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Profile("dev")
@Service
@RequiredArgsConstructor
public class UserSeedingManagement {

      private final UserRepository users;
      private final ApplicationEventPublisher publisher;

      @EventListener(ApplicationReadyEvent.class)
      @Transactional
      public void onApplicationReady() {
            users.findAll().forEach(user -> {
                  publisher.publishEvent(new UserCreated(user.getId(), user.getUsername(), user.getName(), GenderUtils.inspect(user.getGender()), user.getDob(), user.getAvatar()));
            });
      }
}
