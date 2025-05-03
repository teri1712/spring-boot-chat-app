package com.decade.practice.endpoints.auth

import com.decade.practice.core.UserOperations
import com.decade.practice.database.transaction.create
import com.decade.practice.endpoints.validation.StrongPassword
import com.decade.practice.image.ImageStore
import com.decade.practice.model.DefaultAvatar
import com.decade.practice.model.embeddable.ImageSpec
import com.decade.practice.model.entity.User
import com.decade.practice.security.TokenCredentialService
import com.decade.practice.security.jwt.JwtUser
import com.decade.practice.security.jwt.save
import com.decade.practice.security.model.DaoUser
import com.decade.practice.util.ImageUtils
import com.decade.practice.util.TokenUtils
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Past
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.context.SecurityContextRepository
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.net.URI
import java.util.*


const val MAX_USERNAME_LENGTH = 20
const val MIN_USERNAME_LENGTH = 5

@RestController
@RequestMapping("/authentication")
class AuthenticationController(
      private val userOperations: UserOperations,
      private val contextRepo: SecurityContextRepository,
      private val imageStore: ImageStore,
      private val credentialService: TokenCredentialService,
) {

      @PostMapping("/refresh")
      fun refresh(request: HttpServletRequest, response: HttpServletResponse) {
            val refreshToken = TokenUtils.extractRefreshToken(request)
                  ?: throw AccessDeniedException("NO TOKEN REPRESENTED")
            credentialService.validate(refreshToken)

            val claims = credentialService.decodeToken(refreshToken)
            val principal = JwtUser(claims)

            val credential = credentialService.create(claims, refreshToken)
            contextRepo.save(request, response, principal, credential.accessToken)

            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.characterEncoding = Charsets.UTF_8.name()
            response.writer.write(
                  ObjectMapper()
                        .enable(SerializationFeature.INDENT_OUTPUT)
                        .writeValueAsString(credential)
            )
      }

      @PostMapping("/sign-up")
      @PreAuthorize("isAnonymous()")
      @Throws(IOException::class)
      fun signUp(
            request: HttpServletRequest,
            response: HttpServletResponse,
            @Size(
                  min = MIN_USERNAME_LENGTH,
                  max = MAX_USERNAME_LENGTH,
                  message = "Username length must be between "
                          + "$MIN_USERNAME_LENGTH and $MAX_USERNAME_LENGTH "
                          + "characters"
            )
            @NotBlank(message = "Username must not be empty")
            @Pattern(regexp = "\\S+", message = "Username must not contain spaces.")
            @RequestPart username: String,

            @StrongPassword
            @RequestPart password: String,

            @NotBlank
            @RequestPart name: String,

            @NotBlank
            @RequestPart gender: String,

            @Past @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @RequestPart dob: Date,
            @RequestPart("avatar", required = false) file: MultipartFile?,
      ): ResponseEntity<String> {
            val avatar: ImageSpec
            if (file != null) {
                  avatar = imageStore.save(ImageUtils.crop(file.inputStream))
            } else {
                  avatar = DefaultAvatar
            }

            try {
                  val user = userOperations.create(username, password, name, dob, gender, avatar)
                  val context = SecurityContextHolder.createEmptyContext()
                  context.authentication = UsernamePasswordAuthenticationToken(
                        DaoUser(user), password,
                        listOf(SimpleGrantedAuthority("ROLE_USER"))
                  )
                  contextRepo.saveContext(context, request, response)
                  SecurityContextHolder.setContext(context)

                  return ResponseEntity.ok().body("Account created")
            } catch (e: Exception) {
                  e.printStackTrace()
                  if (avatar != DefaultAvatar)
                        imageStore.remove(URI(avatar.uri))
                  if (e is DataIntegrityViolationException) {
                        return ResponseEntity.status(HttpStatus.CONFLICT.value()).body("Username exists")
                  } else {
                        throw e
                  }
            }
      }


      @PostMapping("/password")
      @PreAuthorize(
            "isAuthenticated()"
      )
      fun changePassword(
            request: HttpServletRequest,
            @AuthenticationPrincipal(expression = "id") idOptional: Optional<UUID>,
            @StrongPassword @RequestParam password: String
      ): ResponseEntity<User> {
            val id = idOptional.orElseThrow {
                  // Only application's principals can modify the password
                  throw AccessDeniedException("Operation not supported")
            }
            // Require a modifier token to modify the password
            val refreshToken =
                  TokenUtils.extractRefreshToken(request)
                        ?: throw AccessDeniedException("Missing modifier token")

            val user: User = userOperations.update(id, password, refreshToken)
            return ResponseEntity.ok(user)
      }

}
