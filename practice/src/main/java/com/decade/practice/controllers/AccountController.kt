package com.decade.practice.controllers

import com.decade.practice.core.UserOperations
import com.decade.practice.database.repository.UserRepository
import com.decade.practice.image.ImageStore
import com.decade.practice.model.domain.entity.User
import com.decade.practice.model.local.Account
import com.decade.practice.model.local.AccountEntry
import com.decade.practice.utils.ImageUtils
import jakarta.persistence.OptimisticLockException
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Past
import jakarta.validation.constraints.Size
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.net.URI
import java.util.*

@Controller
@RequestMapping("/account")
class AccountController(
      private val imageStore: ImageStore,
      private val userOperations: UserOperations,
      private val userRepository: UserRepository,
) {


      @PreAuthorize("isAuthenticated() and authentication.authorities.?[authority.toLowerCase().contains('user')].size() > 0")
      @GetMapping
      fun get(
            @AuthenticationPrincipal(expression = "name") username: String,
      ) = ResponseEntity.ok(
            AccountEntry(
                  Account(
                        userRepository.getByUsername(username),
                        credential = null
                  ), emptyList()
            )
      )


      @PutMapping("/information")
      @Throws(OptimisticLockException::class)
      fun modifyInformation(
            @AuthenticationPrincipal(expression = "id") idOptional: Optional<UUID>,
            @Size(min = 4, max = 100, message = "Name length must be between 4-100")
            @RequestParam name: String,
            @Past(message = "Do you have time machine.")
            @RequestParam dob: Date,
            @NotEmpty
            @RequestParam gender: String
      ): ResponseEntity<User> {
            val id = idOptional.orElseThrow {
                  // Only application's principals can modify the information
                  throw AccessDeniedException("Operation not supported")
            }
            return ResponseEntity.ok(userOperations.update(id, name, dob, gender))
      }

      @PostMapping("/avatar")
      @Throws(IOException::class, OptimisticLockException::class)
      fun modifyAvatar(
            @AuthenticationPrincipal(expression = "id") idOptional: Optional<UUID>,
            @RequestParam file: MultipartFile
      ): ResponseEntity<User> {
            val id = idOptional.orElseThrow {
                  // Only application's principals can modify the information
                  throw AccessDeniedException("Operation not supported")
            }
            val cropped = ImageUtils.crop(file.inputStream)
            val avatar = imageStore.save(cropped)
            try {
                  return ResponseEntity.ok(userOperations.update(id, avatar))
            } catch (e: Exception) {
                  imageStore.remove(URI(avatar.uri))
                  throw e
            }
      }
}
