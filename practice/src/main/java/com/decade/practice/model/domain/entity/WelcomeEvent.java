package com.decade.practice.model.domain.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("HELLO WORLD")
public class WelcomeEvent extends TextEvent {

        protected WelcomeEvent() {
                super();
        }

        public WelcomeEvent(Chat chat, User admin, User user) {
                super(chat, admin, welcomeMessage(user));
                setOwner(user);
        }

        private static String welcomeMessage(User user) {
                return "HELLO " + user.getUsername();
        }
}