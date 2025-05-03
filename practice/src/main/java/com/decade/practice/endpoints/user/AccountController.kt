package com.decade.practice.endpoints.user

import com.decade.practice.core.UserOperations
import com.decade.practice.database.repository.UserRepository
import com.decade.practice.database.repository.get
import com.decade.practice.image.ImageStore
import com.decade.practice.model.entity.User
import com.decade.practice.model.local.Account
import com.decade.practice.model.local.AccountEntry
import com.decade.practice.security.jwt.JwtUser
import com.decade.practice.util.ImageUtils
import jakarta.persistence.OptimisticLockException
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Past
import jakarta.validation.constraints.Size
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.net.URI
import java.util.*

@Controller
@RequestMapping("/account")
@Validated
class AccountController(
      private val imageStore: ImageStore,
      private val userOperations: UserOperations,
      private val userRepository: UserRepository,
) {


      @PreAuthorize("isAuthenticated() and hasRole('ROLE_USER')")
      @GetMapping
      fun get(
            @AuthenticationPrincipal(expression = "id") id: UUID,
      ) = ResponseEntity.ok(
            AccountEntry(
                  Account(
                        userRepository.get(id),
                        credential = null
                  ), emptyList()
            )
      )


      @PostMapping("/information")
      @Throws(OptimisticLockException::class)
      fun updateInformation(
            @AuthenticationPrincipal(expression = "id") id: UUID,
            @Size(min = 4, max = 100, message = "Name length must be between 4-100")
            @RequestParam name: String,
            @Past(message = "Do you have time machine.")
            @RequestParam dob: Date,
            @NotEmpty
            @RequestParam gender: String
      ): ResponseEntity<User> {
            return ResponseEntity.ok(userOperations.update(id, name, dob, gender))
      }

      @PostMapping("/avatar")
      @Throws(IOException::class, OptimisticLockException::class)
      fun updateAvatar(
            @AuthenticationPrincipal jwtUser: JwtUser,
            @RequestParam file: MultipartFile
      ): ResponseEntity<User> {
            val cropped = ImageUtils.crop(file.inputStream)
            val avatar = imageStore.save(cropped)
            try {
                  return ResponseEntity.ok(userOperations.update(jwtUser.id, avatar))
            } catch (e: Exception) {
                  imageStore.remove(URI(avatar.uri))
                  throw e
            }
      }
}
