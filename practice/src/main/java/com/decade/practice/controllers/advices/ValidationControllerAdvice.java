package com.decade.practice.controllers.advices;

import com.decade.practice.controllers.validation.ChatIdentifierValidator;
import com.decade.practice.model.domain.embeddable.ChatIdentifier;
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