package com.decade.practice.controllers.advices

import com.decade.practice.controllers.validation.ChatIdentifierValidator
import com.decade.practice.model.domain.embeddable.ChatIdentifier
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.InitBinder


@ControllerAdvice
class ValidationControllerAdvice {
      @InitBinder
      protected fun initBinder(binder: WebDataBinder) {
            if (binder.target is ChatIdentifier)
                  binder.addValidators(ChatIdentifierValidator())
      }
}
