package com.decade.practice;

import com.decade.practice.database.repository.UserRepository;
import com.decade.practice.entities.domain.embeddable.ImageSpec;
import com.decade.practice.entities.domain.entity.Chat;
import com.decade.practice.entities.domain.entity.TextEvent;
import com.decade.practice.usecases.core.EventStore;
import com.decade.practice.usecases.core.OnlineStatistic;
import com.decade.practice.usecases.core.UserOperations;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.util.Date;

@SpringBootApplication
public class ProductionApplication {

      private static final String MALE = "male";

      public static void main(String[] args) {
            SpringApplication app = createApp(ProductionApplication.class);
            ApplicationContext context = app.run(args);
            initialize(context);
      }

      private static boolean isRunningInContainer() {
            return System.getenv().containsKey("DECADE");
      }

      public static SpringApplication createApp(Class<?> appClass) {
            SpringApplication app = new SpringApplication(appClass);
            System.out.println("isRunningInContainer:" + isRunningInContainer());
            System.out.println("environments: " + System.getenv().toString() + "\n\n\n");
            if (isRunningInContainer()) {
                  app.setAdditionalProfiles("docker");
            }
            return app;
      }

      public static void initialize(ApplicationContext context) {
            UserOperations userService = context.getBean(UserOperations.class);
            UserRepository userRepo = context.getBean(UserRepository.class);
            PlatformTransactionManager txManager = context.getBean(PlatformTransactionManager.class);
            TransactionTemplate transactionOperations = new TransactionTemplate(txManager);
            transactionOperations.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);

            EventStore eventStore = context.getBean(EventStore.class);
            OnlineStatistic onlineStat = context.getBean(OnlineStatistic.class);

            userService.create(
                  "Luffy",
                  "Luffy",
                  "Luffy",
                  new Date(),
                  MALE,
                  new ImageSpec("http://192.168.3.104:8080/image?filename=luffy.jpeg", "luffy.jpeg", 512, 512, "jpg"),
                  true
            );
            userService.create(
                  "Nami",
                  "Nami",
                  "Nami",
                  new Date(),
                  MALE,
                  new ImageSpec("http://192.168.3.104:8080/image?filename=nami.jpeg", "nami.jpeg", 512, 512, "jpg"),
                  true
            );
            userService.create(
                  "Chopper",
                  "Chopper",
                  "Chopper",
                  new Date(),
                  MALE,
                  new ImageSpec("http://192.168.3.104:8080/image?filename=chopper.jpeg", "chopper.jpeg", 512, 512, "jpg"),
                  true
            );

            transactionOperations.executeWithoutResult(status -> {
                  var luffy = userRepo.getByUsername("Luffy");
                  var nami = userRepo.getByUsername("Nami");
                  var chopper = userRepo.getByUsername("Chopper");

                  TextEvent event1 = new TextEvent(new Chat(luffy, nami), nami, "Hello");
                  event1.setCreatedTime(System.currentTimeMillis() - 5 * 60 * 1000);
                  eventStore.save(event1);

                  TextEvent event2 = new TextEvent(new Chat(luffy, chopper), chopper, "Ekk");
                  event2.setCreatedTime(System.currentTimeMillis() - 10 * 60 * 1000);
                  eventStore.save(event2);

                  TextEvent event3 = new TextEvent(new Chat(luffy, chopper), chopper, "Vcl");
                  event3.setCreatedTime(System.currentTimeMillis() - 5 * 60 * 1000);
                  eventStore.save(event3);

                  onlineStat.set(nami, Instant.now().getEpochSecond() - 2 * 60);
                  onlineStat.set(chopper, Instant.now().getEpochSecond() - 10 * 60);
            });
      }
}
