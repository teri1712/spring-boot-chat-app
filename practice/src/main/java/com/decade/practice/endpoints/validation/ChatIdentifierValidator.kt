package com.decade.practice.endpoints.validation

import com.decade.practice.model.domain.embeddable.ChatIdentifier
import org.springframework.validation.Errors
import org.springframework.validation.Validator

class ChatIdentifierValidator : Validator {

      override fun supports(clazz: Class<*>): Boolean =
            ChatIdentifier::class.java.isAssignableFrom(clazz)

      override fun validate(target: Any, errors: Errors) {
            val identifier = target as ChatIdentifier
            if (identifier.firstUser >= identifier.secondUser) {
                  errors.reject("Invalid chat identifier")
            }
      }

}