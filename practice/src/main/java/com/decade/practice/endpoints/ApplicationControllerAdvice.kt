package com.decade.practice.endpoints

import com.decade.practice.model.embeddable.ChatIdentifier
import com.decade.practice.model.embeddable.ChatIdentifierValidator
import jakarta.persistence.EntityNotFoundException
import jakarta.persistence.OptimisticLockException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.messaging.handler.annotation.MessageExceptionHandler
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.HandlerMethodValidationException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class ExceptionControllerAdvice : ResponseEntityExceptionHandler() {

    @ExceptionHandler(
        EntityNotFoundException::class,
        NoSuchElementException::class,
        NullPointerException::class
    )
    @ResponseStatus(
        value = HttpStatus.NOT_FOUND,
        reason = "THE REQUESTED RESOURCE NOT FOUND"
    )
    @MessageExceptionHandler(
        EntityNotFoundException::class,
        NoSuchElementException::class,
        NullPointerException::class
    )
    fun handleNoElement(e: Exception) {
        e.printStackTrace()
    }

    @ExceptionHandler(OptimisticLockException::class)
    @ResponseStatus(
        value = HttpStatus.BAD_REQUEST,
        reason = "UPDATE FAILED, TRY AGAIN"
    )
    fun handleLockException() {
    }

    override fun handleHandlerMethodValidationException(
        ex: HandlerMethodValidationException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> {
        return ResponseEntity.badRequest().body(
            ex.allErrors
                .map { it.defaultMessage }
        )
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> {
        return ResponseEntity.badRequest().body(ex.message)
    }
}

@ControllerAdvice
class ValidationAdvice {
    @InitBinder
    protected fun initBinder(binder: WebDataBinder) {
        if (binder.target is ChatIdentifier)
            binder.addValidators(ChatIdentifierValidator())
    }
}
