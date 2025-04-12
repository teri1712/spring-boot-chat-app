package com.decade.practice.endpoints.user

import com.decade.practice.database.UserOperations
import com.decade.practice.image.ImageStore
import com.decade.practice.model.embeddable.ImageSpec
import com.decade.practice.model.entity.User
import com.decade.practice.security.model.JwtUser
import com.decade.practice.util.ImageUtils
import jakarta.persistence.OptimisticLockException
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Past
import jakarta.validation.constraints.Size
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.util.*

@Controller
@RequestMapping("/user")
@Validated
class UserController(
    private val imageStore: ImageStore,
    private val userOperations: UserOperations
) {

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
        httpRequest: HttpServletRequest,
        @AuthenticationPrincipal jwtUser: JwtUser,
        @RequestParam file: MultipartFile
    ): ResponseEntity<User> {
        val cropped = ImageUtils.crop(file.inputStream)
        val url = imageStore.save(cropped)
        val avatar = ImageSpec(url.toString())
        try {
            return ResponseEntity.ok(userOperations.update(jwtUser.id, avatar))
        } catch (e: Exception) {
            imageStore.remove(url)
            throw e
        }
    }
}
