package com.decade.practice

import org.springframework.boot.autoconfigure.SpringBootApplication


@SpringBootApplication
class DevelopmentApplication

fun main(args: Array<String>) {
      val app = createApp(DevelopmentApplication::class.java)
      app.setAdditionalProfiles("development")
      val context = app.run(*args)
      initialize(context)
}