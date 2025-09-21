package com.decade.practice.infra.bootstrap;

import com.decade.practice.application.usecases.EventStore;
import com.decade.practice.application.usecases.UserService;
import com.decade.practice.domain.embeddables.ImageSpec;
import com.decade.practice.domain.entities.*;
import com.decade.practice.domain.repositories.AdminRepository;
import com.decade.practice.domain.repositories.ThemeRepository;
import com.decade.practice.domain.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
        @Qualifier("jpaAdminRepository")
        private AdminRepository adminRepo;

        @Autowired
        @Qualifier("jpaThemeRepository")
        private ThemeRepository themeRepository;

        @Autowired
        private UserService userService;

        @Autowired

        private UserRepository userRepo;

        @Autowired
        private EventStore chatEventStore;

        @Value("${admin.username}")
        private String adminUsername;

        @Value("${admin.password}")
        private String adminPassword;


        @Value("${app.host.address}")
        private String hostAddress;


        public void run() {
                if (adminRepo.getOrNull() != null) {
                        return;
                }

                adminRepo.save(new Admin(adminUsername, adminPassword));
                em.flush();
                try {
                        Resource[] resources = new PathMatchingResourcePatternResolver().getResources("classpath*:static/theme/*");
                        for (int i = 0; i < resources.length; i++) {
                                Resource resource = resources[i];
                                themeRepository.save(new Theme(i + 1, new ImageSpec(hostAddress + "/theme/" + resource.getFilename(), resource.getFilename(), 512, 512, "jpg")));
                        }
                } catch (IOException e) {
                        throw new RuntimeException(e);
                }

                userService.create(
                        "Luffy",
                        "Luffy",
                        "Luffy",
                        new Date(),
                        "MALE",
                        new ImageSpec(hostAddress + "/medias/luffy.jpeg", "luffy.jpeg", 512, 512, "jpeg"),
                        true
                );
                userService.create(
                        "Nami",
                        "Nami",
                        "Nami",
                        new Date(),
                        "MALE",
                        new ImageSpec(hostAddress + "/medias/nami.jpeg", "nami.jpeg", 512, 512, "jpeg"),
                        true
                );
                userService.create(
                        "Chopper",
                        "Chopper",
                        "Chopper",
                        new Date(),
                        "MALE",
                        new ImageSpec(hostAddress + "/medias/chopper.jpeg", "chopper.jpeg", 512, 512, "jpeg"),
                        true
                );
                userService.create(
                        "Zoro",
                        "Zoro",
                        "Zoro",
                        new Date(),
                        "MALE",
                        new ImageSpec(hostAddress + "/medias/zoro.jpg", "zoro.jpg", 512, 512, "jpg"),
                        true
                );

                User luffy = userRepo.findByUsername("Luffy");
                User nami = userRepo.findByUsername("Nami");
                User chopper = userRepo.findByUsername("Chopper");
                Chat luffyNamiChat = new Chat(luffy, nami);
                em.persist(luffyNamiChat);
                Chat luffyChopperChat = new Chat(luffy, chopper);
                em.persist(luffyChopperChat);
                Chat namiChopperChat = new Chat(nami, chopper);
                em.persist(namiChopperChat);
                em.flush();

                TextEvent event1 = new TextEvent(luffyNamiChat, nami, "Hello");
                event1.setCreatedTime(System.currentTimeMillis() - 5 * 60 * 1000);
                chatEventStore.save(event1);

                TextEvent event2 = new TextEvent(luffyChopperChat, chopper, "Ekk");
                event2.setCreatedTime(System.currentTimeMillis() - 10 * 60 * 1000);
                chatEventStore.save(event2);

                TextEvent event3 = new TextEvent(namiChopperChat, chopper, "Vcl");
                event3.setCreatedTime(System.currentTimeMillis() - 5 * 60 * 1000);
                chatEventStore.save(event3);
        }
}
