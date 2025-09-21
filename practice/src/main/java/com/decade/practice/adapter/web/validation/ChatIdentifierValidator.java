package com.decade.practice.adapter.web.validation;

import com.decade.practice.application.usecases.ConversationRepository;
import com.decade.practice.domain.embeddables.ChatIdentifier;
import com.decade.practice.domain.entities.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class ChatIdentifierValidator implements Validator {

        private final ConversationRepository conversationRepository;

        public ChatIdentifierValidator(ConversationRepository conversationRepository) {
                this.conversationRepository = conversationRepository;
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
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String username = authentication.getName();
                User user = conversationRepository.getUser(username);
                boolean allowed =
                        identifier.getFirstUser().equals(user.getId())
                                || identifier.getSecondUser().equals(user.getId());
                if (!allowed) {
                        errors.reject("You are not allowed to access this chat");
                }
        }
}