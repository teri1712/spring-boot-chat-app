package com.decade.practice.web.validation;

import com.decade.practice.data.repositories.UserRepository;
import com.decade.practice.model.domain.embeddable.ChatIdentifier;
import com.decade.practice.model.domain.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class ChatIdentifierValidator implements Validator {

        private final UserRepository userRepository;

        public ChatIdentifierValidator(UserRepository userRepository) {
                this.userRepository = userRepository;
        }

        @Override
        public boolean supports(Class<?> clazz) {
                return ChatIdentifier.class.isAssignableFrom(clazz);
        }

        @Override
        public void validate(Object target, Errors errors) {
                ChatIdentifier chat = (ChatIdentifier) target;
                if (chat.getFirstUser().compareTo(chat.getSecondUser()) > 0) {
                        errors.reject("Invalid chat identifier");
                }
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                User user = userRepository.getByUsername(authentication.getName());
                boolean allowed =
                        chat.getFirstUser().equals(user.getId())
                                || chat.getSecondUser().equals(user.getId());
                if (!allowed) {
                        errors.reject("You are not allowed to access this chat");
                }
        }
}