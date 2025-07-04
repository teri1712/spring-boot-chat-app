package com.decade.practice.controllers.validation;

import com.decade.practice.model.domain.embeddable.ChatIdentifier;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class ChatIdentifierValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return ChatIdentifier.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ChatIdentifier identifier = (ChatIdentifier) target;
        if (identifier.getFirstUser().compareTo(identifier.getSecondUser()) >= 0) {
            errors.reject("Invalid chat identifier");
        }
    }
}