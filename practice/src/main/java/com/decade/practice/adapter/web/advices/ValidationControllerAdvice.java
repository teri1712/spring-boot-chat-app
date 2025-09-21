package com.decade.practice.adapter.web.advices;

import com.decade.practice.adapter.web.validation.ChatIdentifierValidator;
import com.decade.practice.application.usecases.ConversationRepository;
import com.decade.practice.domain.embeddables.ChatIdentifier;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

@ControllerAdvice
public class ValidationControllerAdvice {

        private final ConversationRepository conversationRepository;

        public ValidationControllerAdvice(ConversationRepository conversationRepository) {
                this.conversationRepository = conversationRepository;
        }

        @InitBinder
        protected void initBinder(WebDataBinder binder) {
                if (binder.getTarget() instanceof ChatIdentifier) {
                        binder.addValidators(new ChatIdentifierValidator(conversationRepository));
                }
        }
}