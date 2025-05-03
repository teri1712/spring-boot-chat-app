package com.decade.practice

import com.decade.practice.core.EventStore
import com.decade.practice.core.UserOperations
import com.decade.practice.database.repository.UserRepository
import com.decade.practice.model.embeddable.ImageSpec
import com.decade.practice.model.entity.Chat
import com.decade.practice.model.entity.MALE
import com.decade.practice.model.entity.TextEvent
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.transaction.support.TransactionOperations
import java.util.*


@SpringBootApplication
class TestApplication

fun main(args: Array<String>) {
      val app = SpringApplication(TestApplication::class.java)
      app.setAdditionalProfiles("testing")
      val context = app.run(*args)
      val userService = context.getBean(UserOperations::class.java)
      val userRepo = context.getBean(UserRepository::class.java)
      val transactionOperations = context.getBean(TransactionOperations::class.java)
      val eventStore = context.getBean(EventStore::class.java)

      transactionOperations.executeWithoutResult {

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
      }
}