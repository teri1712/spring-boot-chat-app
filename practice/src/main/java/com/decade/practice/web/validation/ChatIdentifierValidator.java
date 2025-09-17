package com.decade.practice.web.validation;

import com.decade.practice.models.domain.embeddable.ChatIdentifier;
import com.decade.practice.models.domain.entity.Chat;
import com.decade.practice.usecases.ChatOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class ChatIdentifierValidator implements Validator {

        private final ChatOperations chatOperations;

        public ChatIdentifierValidator(ChatOperations chatOperations) {
                this.chatOperations = chatOperations;
        }

        @Override
        public boolean supports(Class<?> clazz) {
                return ChatIdentifier.class.isAssignableFrom(clazz);
        }

        @Override
        public void validate(Object target, Errors errors) {
                ChatIdentifier identifier = (ChatIdentifier) target;
                if (identifier.getFirstUser().compareTo(identifier.getSecondUser()) > 0) {
                        errors.reject("Invalid chat identifier");
                }
                Chat chat = chatOperations.getOrCreateChat(identifier);
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String username = authentication.getName();
                boolean allowed =
                        chat.getFirstUser().getUsername().equals(username)
                                || chat.getSecondUser().getUsername().equals(username);
                if (!allowed) {
                        errors.reject("You are not allowed to access this chat");
                }
        }
}