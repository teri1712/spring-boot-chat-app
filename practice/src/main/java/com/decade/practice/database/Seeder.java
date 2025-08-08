package com.decade.practice.database;

import com.decade.practice.database.repositories.AdminRepository;
import com.decade.practice.database.repositories.UserRepository;
import com.decade.practice.entities.domain.embeddable.ImageSpec;
import com.decade.practice.entities.domain.entity.Admin;
import com.decade.practice.entities.domain.entity.Chat;
import com.decade.practice.entities.domain.entity.TextEvent;
import com.decade.practice.entities.domain.entity.User;
import com.decade.practice.usecases.core.EventStore;
import com.decade.practice.usecases.core.UserOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Transactional
public class Seeder {

        @Autowired
        private AdminRepository adminRepo;

        @Autowired
        private UserOperations userOperations;

        @Autowired
        private EventStore eventStore;
        @Autowired
        private UserRepository userRepo;

        @Value("${admin.username}")
        private String adminUsername;

        @Value("${admin.password}")
        private String adminPassword;

        void run() {
                if (adminRepo.getOrNull() != null) {
                        return;
                }
                adminRepo.save(new Admin(adminUsername, adminPassword));
                userOperations.create(
                        "Luffy",
                        "Luffy",
                        "Luffy",
                        new Date(),
                        "MALE",
                        new ImageSpec("http://localhost:8080/image?filename=luffy.jpeg", "luffy.jpeg", 512, 512, "jpg"),
                        true
                );
                userOperations.create(
                        "Nami",
                        "Nami",
                        "Nami",
                        new Date(),
                        "MALE",
                        new ImageSpec("http://localhost:8080/image?filename=nami.jpeg", "nami.jpeg", 512, 512, "jpg"),
                        true
                );
                userOperations.create(
                        "Chopper",
                        "Chopper",
                        "Chopper",
                        new Date(),
                        "MALE",
                        new ImageSpec("http://localhost:8080/image?filename=chopper.jpeg", "chopper.jpeg", 512, 512, "jpg"),
                        true
                );

                User luffy = userRepo.getByUsername("Luffy");
                User nami = userRepo.getByUsername("Nami");
                User chopper = userRepo.getByUsername("Chopper");

                TextEvent event1 = new TextEvent(new Chat(luffy, nami), nami, "Hello");
                event1.setCreatedTime(System.currentTimeMillis() - 5 * 60 * 1000);
                eventStore.save(event1);

                TextEvent event2 = new TextEvent(new Chat(luffy, chopper), chopper, "Ekk");
                event2.setCreatedTime(System.currentTimeMillis() - 10 * 60 * 1000);
                eventStore.save(event2);

                TextEvent event3 = new TextEvent(new Chat(luffy, chopper), chopper, "Vcl");
                event3.setCreatedTime(System.currentTimeMillis() - 5 * 60 * 1000);
                eventStore.save(event3);

        }
}
