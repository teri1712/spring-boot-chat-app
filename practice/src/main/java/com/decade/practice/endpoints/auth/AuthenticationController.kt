package com.decade.practice.endpoints.auth

import com.decade.practice.database.UserOperations
import com.decade.practice.database.repository.UserRepository
import com.decade.practice.database.transaction.create
import com.decade.practice.database.transaction.createOauth2User
import com.decade.practice.endpoints.validation.StrongPassword
import com.decade.practice.image.ImageStore
import com.decade.practice.model.DefaultAvatar
import com.decade.practice.model.embeddable.ImageSpec
import com.decade.practice.model.entity.User
import com.decade.practice.security.LoginSuccessStrategy
import com.decade.practice.security.model.CredentialModifierInformation
import com.decade.practice.security.model.DaoUser
import com.decade.practice.util.ImageUtils
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Past
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
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
    private val userRepository: UserRepository,
    private val userOperations: UserOperations,
    private val contextRepo: SecurityContextRepository,
    private val strategy: LoginSuccessStrategy,
    private val imageStore: ImageStore,
) {

    @PostMapping("/oauth2/token/login")
    @PreAuthorize(
        "isAuthenticated() and authentication instanceof T(org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken)"
    )
    fun logIn(
        authentication: JwtAuthenticationToken,
        request: HttpServletRequest,
        httpResponse: HttpServletResponse
    ) {
        val username = authentication.name
        val name = authentication.tokenAttributes["name"].toString()
        var user: User
        try {
            user = userOperations.createOauth2User(username, name)
        } catch (ignored: DataIntegrityViolationException) {
            user = userRepository.getByUsername(username)
        }
        strategy.onAuthenticationSuccess(request, httpResponse, user)
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
        "isAuthenticated() and principal instanceof T(com.decade.practice.security.model.CredentialModifierInformation)"
    )
    fun changePassword(
        httpRequest: HttpServletRequest,
        @AuthenticationPrincipal principal: CredentialModifierInformation,
        @StrongPassword @RequestParam password: String
    ) = ResponseEntity.ok(userOperations.update(principal, password))

}
