package com.decade.practice.controllers.rest

import com.decade.practice.core.TokenCredentialService
import com.decade.practice.core.UserOperations
import com.decade.practice.database.transaction.create
import com.decade.practice.image.ImageStore
import com.decade.practice.model.domain.DefaultAvatar
import com.decade.practice.model.domain.embeddable.ImageSpec
import com.decade.practice.model.dto.SignUpRequest
import com.decade.practice.security.model.DaoUser
import com.decade.practice.utils.ImageUtils
import com.decade.practice.utils.TokenUtils
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.web.context.SecurityContextRepository
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.net.URI

// Defines maximum and minimum length for usernames
const val MAX_USERNAME_LENGTH = 20
const val MIN_USERNAME_LENGTH = 5

/**
 * REST controller for handling authentication-related requests.
 * Provides endpoints for refreshing tokens, signing up, and changing passwords.
 */
@RestController
@RequestMapping("/authentication")
class AuthenticationController(
      private val userOperations: UserOperations,
      private val contextRepo: SecurityContextRepository,
      private val imageStore: ImageStore,
      private val credentialService: TokenCredentialService,
) {
      /**
       * Handles OAuth2 authentication exchange.
       * Creates or finds a user based on the JWT claims from the OAuth2 provider,
       * then redirects to the account endpoint to get the AccountEntry.
       */
      @PostMapping("/oauth2")
      fun exchange(
            @AuthenticationPrincipal jwt: Jwt,
            request: HttpServletRequest,
            response: HttpServletResponse
      ) {
            // Use jwt sub as username
            val username = jwt.subject

            // Extract the user's name and picture from the JWT claims
            val claims = jwt.claims
            val name = claims["name"].toString()
            val picture = claims["picture"].toString()
            try {
                  // Create the user based on the provided username
                  userOperations.createOauth2User(username, name, picture)
            } catch (ignored: DataIntegrityViolationException) {
            }

            // Redirect to the account endpoint to get the AccountEntry
            request.getRequestDispatcher("/account").forward(request, response)
      }

      /**
       * Refreshes an access token using a valid refresh token.
       * Throws an exception if the refresh token is missing or invalid.
       */
      @PostMapping("/refresh")
      fun refresh(request: HttpServletRequest, response: HttpServletResponse) {
            val refreshToken = TokenUtils.extractRefreshToken(request)
                  ?: throw AccessDeniedException("NO TOKEN REPRESENTED")
            // Validate the provided refresh token
            credentialService.validate(refreshToken)

            // Decode the token claims and create a new credential object
            val claims = credentialService.decodeToken(refreshToken)
            val credential = credentialService.create(claims, refreshToken)

            // Write the new credential as JSON to the response
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.characterEncoding = Charsets.UTF_8.name()
            response.writer.write(
                  ObjectMapper()
                        .enable(SerializationFeature.INDENT_OUTPUT)
                        .writeValueAsString(credential)
            )
      }

      /**
       * Creates a new user account.
       * If an avatar file is included, it will be stored using the ImageStore.
       * Otherwise, a default avatar is used.
       */
      @PostMapping("/sign-up")
      @PreAuthorize("isAnonymous()")
      @Throws(IOException::class)
      fun signUp(
            request: HttpServletRequest,
            response: HttpServletResponse,
            @RequestPart("information") @Valid information: SignUpRequest,
            @RequestPart("file", required = false) file: MultipartFile?,
      ): ResponseEntity<String> {
            // Determine which avatar to use
            val avatar: ImageSpec = if (file != null) {
                  imageStore.save(ImageUtils.crop(file.inputStream))
            } else {
                  DefaultAvatar
            }

            return try {
                  // Create the user using the provided information
                  val user = userOperations.create(
                        information.username,
                        information.password,
                        information.name,
                        information.dob,
                        information.gender,
                        avatar
                  )

                  // Build and store an authentication token into the SecurityContext
                  val context = SecurityContextHolder.createEmptyContext()
                  context.authentication = UsernamePasswordAuthenticationToken(
                        DaoUser(user), information.password,
                        listOf(SimpleGrantedAuthority("ROLE_USER"))
                  )
                  contextRepo.saveContext(context, request, response)
                  SecurityContextHolder.setContext(context)

                  // Account creation was successful
                  ResponseEntity.ok().body("Account created")
            } catch (e: Exception) {
                  e.printStackTrace()
                  // Remove uploaded avatar if creation failed (unless using the default avatar)
                  if (avatar != DefaultAvatar)
                        imageStore.remove(URI(avatar.uri))

                  // Handle the specific case of a username conflict
                  if (e is DataIntegrityViolationException) {
                        ResponseEntity.status(HttpStatus.CONFLICT.value()).body("Username exists")
                  } else {
                        // Rethrow any other exception
                        throw e
                  }
            }
      }


}