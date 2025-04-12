package com.decade.practice.database.transaction

import com.decade.practice.database.UserOperations
import com.decade.practice.database.repository.AdminRepository
import com.decade.practice.database.repository.UserRepository
import com.decade.practice.database.repository.get
import com.decade.practice.event.AccountEventListener
import com.decade.practice.model.DefaultAvatar
import com.decade.practice.model.embeddable.ImageSpec
import com.decade.practice.model.entity.*
import com.decade.practice.security.model.CredentialModifierInformation
import jakarta.persistence.EntityManager
import jakarta.persistence.OptimisticLockException
import jakarta.persistence.PersistenceContext
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
class UserService(
    private val userRepo: UserRepository,
    private val adminRepo: AdminRepository,
    private val encoder: PasswordEncoder,
    private val accountListeners: List<AccountEventListener>,
) : UserOperations {

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun create(
        username: String,
        password: String,
        name: String,
        dob: Date,
        gender: String,
        avatar: ImageSpec,
        usernameAsIdentifier: Boolean
    ): User {

        val user = User(username, encoder.encode(password), name, dob).also {
            it.gender.add(gender)
            it.avatar = avatar
            if (usernameAsIdentifier) {
                it.id = UUID.nameUUIDFromBytes(username.toByteArray())
            }
        }
        val admin = adminRepo.get()
        val adminChat = Chat(admin, user)
        val welcomeEvent = WelcomeEvent(adminChat, admin, user)
        val head = Edge(owner = user, from = adminChat, dest = null, event = welcomeEvent, head = true)
        welcomeEvent.edges.add(head)
        em.persist(welcomeEvent)

        accountListeners.forEach {
            it.onAccountCreated(user)
        }
        return user
    }

    @Throws(OptimisticLockException::class)
    override fun update(
        id: UUID,
        name: String,
        birthday: Date,
        gender: String
    ) = userRepo.getOptimistic(id).also {
        it.name = name
        it.dob = birthday
        it.gender.add(gender)

    }

    @Throws(OptimisticLockException::class)
    override fun update(id: UUID, avatar: ImageSpec): User {
        val u = userRepo.getOptimistic(id)
        u.avatar = avatar
        return u
    }


    @Throws(AccessDeniedException::class, OptimisticLockException::class)
    override fun update(credential: CredentialModifierInformation, password: String): User {
        val u = userRepo.getOptimisticIncrement(credential.getUsername())
        if (u.passwordVersion != credential.getPasswordVersion())
            throw AccessDeniedException("EXPIRED CREDENTIAL")
        u.password = encoder.encode(password)
        accountListeners.forEach {
            it.onPasswordChanged(u)
        }
        return u
    }

    @Throws(AccessDeniedException::class)
    override fun validateCredential(credential: CredentialModifierInformation): User {
        val user = userRepo.getByUsername(credential.getUsername())!!
        if (user.passwordVersion != credential.getPasswordVersion())
            throw AccessDeniedException("EXPIRED CREDENTIAL")
        return user
    }

}


@Throws(DataIntegrityViolationException::class)
fun UserOperations.create(
    username: String,
    password: String,
    name: String,
    dob: Date,
    gender: String,
    avatar: ImageSpec
): User {
    return create(username, password, name, dob, gender, avatar, false)
}

@Throws(DataIntegrityViolationException::class)
fun UserOperations.create(
    username: String,
    password: String,
    usernameAsIdentifier: Boolean = false
): User {
    return create(username, password, username, Date(), MALE, DefaultAvatar, usernameAsIdentifier)
}

@Throws(DataIntegrityViolationException::class)
fun UserOperations.createOauth2User(username: String, name: String): User {
    val password = UUID.randomUUID().toString()
    return create(username, password, name, Date(), MALE, DefaultAvatar, false)
}
