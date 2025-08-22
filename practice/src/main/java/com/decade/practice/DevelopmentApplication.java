package com.decade.practice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DevelopmentApplication {

        public static void main(String[] args) {
                SpringApplication app = Application.createApp(DevelopmentApplication.class);
                app.setAdditionalProfiles("development");
                var context = app.run(args);
                Application.seeding(context);
        }
}
