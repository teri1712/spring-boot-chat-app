package com.decade.practice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication app = createApp(Application.class);
        app.run(args);
    }

    public static SpringApplication createApp(Class<?> appClass) {
        SpringApplication app = new SpringApplication(appClass);
        return app;
    }

}
