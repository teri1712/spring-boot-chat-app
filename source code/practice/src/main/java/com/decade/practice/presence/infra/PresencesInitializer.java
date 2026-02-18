package com.decade.practice.presence.infra;

import com.decade.practice.presence.application.ports.in.PresenceSetter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class PresencesInitializer implements ApplicationRunner {

    private final PresenceSetter presenceSetter;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        presenceSetter.set(UUID.fromString("00000000-0000-0000-0000-000000000003"), "Nami", "Nami", "https://i.pinimg.com/736x/74/d2/0f/74d20f3bdbeeaea11da2bf70cd1ef60a.jpg", Instant.now());
        presenceSetter.set(UUID.fromString("00000000-0000-0000-0000-000000000004"), "Chopper", "Chopper", "https://i.pinimg.com/736x/ec/a7/0f/eca70f7274805de4be9553a9632778bf.jpg", Instant.now());
        presenceSetter.set(UUID.fromString("00000000-0000-0000-0000-000000000005"), "Zoro", "Zoro", "https://i.pinimg.com/736x/79/e2/c9/79e2c9402014ead1eebf6c9f184c5bf8.jpg", Instant.now());

    }

}
