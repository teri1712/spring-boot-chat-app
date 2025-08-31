package com.decade.practice.data.database;

import com.decade.practice.data.repositories.AdminRepository;
import com.decade.practice.data.repositories.ThemeRepository;
import com.decade.practice.data.repositories.UserRepository;
import com.decade.practice.model.domain.embeddable.ImageSpec;
import com.decade.practice.model.domain.entity.*;
import com.decade.practice.usecases.EventStore;
import com.decade.practice.usecases.UserOperations;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Date;

@Transactional
public class Seeder {

        @PersistenceContext
        private EntityManager em;

        @Autowired
        private Environment environment;

        @Autowired
        private AdminRepository adminRepo;

        @Autowired
        private ThemeRepository themeRepository;

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

        public void run() {
                if (adminRepo.getOrNull() != null) {
                        return;
                }

                adminRepo.save(new Admin(adminUsername, adminPassword));
                adminRepo.flush();
                try {
                        Resource[] resources = new PathMatchingResourcePatternResolver().getResources("classpath*:static/theme/*");
                        for (int i = 0; i < resources.length; i++) {
                                Resource resource = resources[i];
                                themeRepository.save(new Theme(i + 1, new ImageSpec("http://localhost:8080/theme/" + resource.getFilename(), resource.getFilename(), 512, 512, "jpg")));
                        }
                } catch (IOException e) {
                        throw new RuntimeException(e);
                }

                userOperations.create(
                        "Luffy",
                        "Luffy",
                        "Luffy",
                        new Date(),
                        "MALE",
                        new ImageSpec("http://localhost:8080/medias/luffy.jpeg", "luffy.jpeg", 512, 512, "jpeg"),
                        true
                );
                userOperations.create(
                        "Nami",
                        "Nami",
                        "Nami",
                        new Date(),
                        "MALE",
                        new ImageSpec("http://localhost:8080/medias/nami.jpeg", "nami.jpeg", 512, 512, "jpeg"),
                        true
                );
                userOperations.create(
                        "Chopper",
                        "Chopper",
                        "Chopper",
                        new Date(),
                        "MALE",
                        new ImageSpec("http://localhost:8080/medias/chopper.jpeg", "chopper.jpeg", 512, 512, "jpeg"),
                        true
                );
                userOperations.create(
                        "Zoro",
                        "Zoro",
                        "Zoro",
                        new Date(),
                        "MALE",
                        new ImageSpec("http://localhost:8080/medias/zoro.jpg", "zoro.jpg", 512, 512, "jpg"),
                        true
                );

                User luffy = userRepo.getByUsername("Luffy");
                User nami = userRepo.getByUsername("Nami");
                User chopper = userRepo.getByUsername("Chopper");
                Chat luffyNamiChat = new Chat(luffy, nami);
                em.persist(luffyNamiChat);
                Chat luffyChopperChat = new Chat(luffy, chopper);
                em.persist(luffyChopperChat);
                Chat namiChopperChat = new Chat(nami, chopper);
                em.persist(namiChopperChat);
                em.flush();

                TextEvent event1 = new TextEvent(luffyNamiChat, nami, "Hello");
                event1.setCreatedTime(System.currentTimeMillis() - 5 * 60 * 1000);
                eventStore.save(event1);

                TextEvent event2 = new TextEvent(luffyChopperChat, chopper, "Ekk");
                event2.setCreatedTime(System.currentTimeMillis() - 10 * 60 * 1000);
                eventStore.save(event2);

                TextEvent event3 = new TextEvent(namiChopperChat, chopper, "Vcl");
                event3.setCreatedTime(System.currentTimeMillis() - 5 * 60 * 1000);
                eventStore.save(event3);
        }
}
