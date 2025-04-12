package com.decade.practice.model.embeddable

import jakarta.persistence.Embeddable
import org.springframework.validation.Errors
import org.springframework.validation.Validator
import java.io.Serializable
import java.util.*


@Embeddable
data class ChatIdentifier(
    var firstUser: UUID,
    var secondUser: UUID
) : Serializable {
    override fun toString(): String = "$firstUser+$secondUser"
}

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