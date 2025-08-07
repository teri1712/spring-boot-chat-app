package com.decade.practice.web.advices;

import com.decade.practice.entities.domain.embeddable.ChatIdentifier;
import com.decade.practice.web.validation.ChatIdentifierValidator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

@ControllerAdvice
public class ValidationControllerAdvice {

      @InitBinder
      protected void initBinder(WebDataBinder binder) {
            if (binder.getTarget() instanceof ChatIdentifier) {
                  binder.addValidators(new ChatIdentifierValidator());
            }
      }
}