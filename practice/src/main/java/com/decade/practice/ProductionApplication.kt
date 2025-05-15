package com.decade.practice

import com.decade.practice.core.EventStore
import com.decade.practice.core.OnlineStatistic
import com.decade.practice.core.UserOperations
import com.decade.practice.database.repository.UserRepository
import com.decade.practice.model.domain.embeddable.ImageSpec
import com.decade.practice.model.domain.entity.Chat
import com.decade.practice.model.domain.entity.MALE
import com.decade.practice.model.domain.entity.TextEvent
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.ApplicationContext
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.support.TransactionTemplate
import java.time.Instant
import java.util.*


@SpringBootApplication
class ProductionApplication

fun main(args: Array<String>) {
      val app = createApp(ProductionApplication::class.java)
      val context = app.run(*args)
      initialize(context)
}

private fun isRunningInContainer() =
      System.getenv().containsKey("DECADE")

fun createApp(appClass: Class<*>): SpringApplication {
      val app = SpringApplication(appClass)
      println("isRunningInContainer:" + isRunningInContainer())
      println("environments: " + System.getenv().toString() + "\n\n\n")
      if (isRunningInContainer()) {
            app.setAdditionalProfiles("docker")
      }
      return app
}

fun initialize(context: ApplicationContext) {
      val userService = context.getBean(UserOperations::class.java)
      val userRepo = context.getBean(UserRepository::class.java)
      val txManager = context.getBean(PlatformTransactionManager::class.java)
      val transactionOperations = TransactionTemplate(txManager).apply {
            isolationLevel = TransactionDefinition.ISOLATION_READ_COMMITTED
      }
      val eventStore = context.getBean(EventStore::class.java)
      val onlineStat = context.getBean(OnlineStatistic::class.java)

      userService.create(
            "Luffy",
            "Luffy",
            "Luffy",
            Date(),
            MALE,
            ImageSpec("http://192.168.3.104:8080/image?filename=luffy.jpeg", "luffy.jpeg"),
            true
      )
      userService.create(
            "Nami",
            "Nami",
            "Nami",
            Date(),
            MALE,
            ImageSpec("http://192.168.3.104:8080/image?filename=nami.jpeg", "nami.jpeg"),
            true
      )
      userService.create(
            "Chopper",
            "Chopper",
            "Chopper",
            Date(),
            MALE,
            ImageSpec("http://192.168.3.104:8080/image?filename=chopper.jpeg", "chopper.jpeg"),
            true
      )

      transactionOperations.executeWithoutResult {

            val luffy = userRepo.getByUsername("Luffy")
            val nami = userRepo.getByUsername("Nami")
            val chopper = userRepo.getByUsername("Chopper")
            eventStore.save(event = TextEvent(Chat(luffy, nami), nami, "Hello").apply {
                  createdTime = System.currentTimeMillis() - 5 * 60 * 1000
            })
            eventStore.save(event = TextEvent(Chat(luffy, chopper), chopper, "Ekk").apply {
                  createdTime = System.currentTimeMillis() - 10 * 60 * 1000
            })
            eventStore.save(event = TextEvent(Chat(luffy, chopper), chopper, "Vcl").apply {
                  createdTime = System.currentTimeMillis() - 5 * 60 * 1000
            })

            onlineStat.set(nami, Instant.now().epochSecond - 2 * 60)
            onlineStat.set(chopper, Instant.now().epochSecond - 10 * 60)
      }
}