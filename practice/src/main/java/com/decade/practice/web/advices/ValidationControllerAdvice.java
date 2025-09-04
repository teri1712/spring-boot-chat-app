package com.decade.practice.web.advices;

import com.decade.practice.data.repositories.UserRepository;
import com.decade.practice.models.domain.embeddable.ChatIdentifier;
import com.decade.practice.web.validation.ChatIdentifierValidator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

@ControllerAdvice
public class ValidationControllerAdvice {

        private final UserRepository userRepository;

        public ValidationControllerAdvice(UserRepository userRepository) {
                this.userRepository = userRepository;
        }

        @InitBinder
        protected void initBinder(WebDataBinder binder) {
                if (binder.getTarget() instanceof ChatIdentifier) {
                        binder.addValidators(new ChatIdentifierValidator(userRepository));
                }
        }
}