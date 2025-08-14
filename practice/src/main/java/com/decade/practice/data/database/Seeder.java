package com.decade.practice.data.database;

import com.decade.practice.common.SelfAwareBean;
import com.decade.practice.data.repositories.AdminRepository;
import com.decade.practice.data.repositories.UserRepository;
import com.decade.practice.model.domain.embeddable.ImageSpec;
import com.decade.practice.model.domain.entity.Admin;
import com.decade.practice.model.domain.entity.Chat;
import com.decade.practice.model.domain.entity.TextEvent;
import com.decade.practice.model.domain.entity.User;
import com.decade.practice.usecases.EventStore;
import com.decade.practice.usecases.UserOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

public class Seeder extends SelfAwareBean {

        @Autowired
        private AdminRepository adminRepo;

        @Autowired
        private UserOperations userOperations;

        @Autowired
        private UserRepository userRepo;

        @Autowired
        private EventStore eventStore;

        @Value("${admin.username}")
        private String adminUsername;

        @Value("${admin.password}")
        private String adminPassword;

        @Transactional(propagation = Propagation.REQUIRES_NEW)
        void initUsers() {
                if (adminRepo.getOrNull() != null) {
                        return;
                }
                adminRepo.save(new Admin(adminUsername, adminPassword));
                adminRepo.flush();
                userOperations.create(
                        "Luffy",
                        "Luffy",
                        "Luffy",
                        new Date(),
                        "MALE",
                        new ImageSpec("http://localhost:8080/image?filename=luffy.jpg", "luffy.jpg", 512, 512, "jpg"),
                        true
                );
                userOperations.create(
                        "Nami",
                        "Nami",
                        "Nami",
                        new Date(),
                        "MALE",
                        new ImageSpec("http://localhost:8080/image?filename=nami.jpg", "nami.jpg", 512, 512, "jpg"),
                        true
                );
                userOperations.create(
                        "Chopper",
                        "Chopper",
                        "Chopper",
                        new Date(),
                        "MALE",
                        new ImageSpec("http://localhost:8080/image?filename=chopper.jpg", "chopper.jpg", 512, 512, "jpg"),
                        true
                );
        }

        @Transactional
        void initEvents() {
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

        void run() {
                ((Seeder) getSelf()).initUsers();
                ((Seeder) getSelf()).initEvents();
        }
}
