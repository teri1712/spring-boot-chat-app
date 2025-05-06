package com.decade.practice.database.transaction

import com.decade.practice.core.UserOperations
import com.decade.practice.core.common.SelfAwareBean
import com.decade.practice.database.repository.AdminRepository
import com.decade.practice.database.repository.UserRepository
import com.decade.practice.database.repository.get
import com.decade.practice.event.AccountEventListener
import com.decade.practice.model.domain.DefaultAvatar
import com.decade.practice.model.domain.embeddable.ImageSpec
import com.decade.practice.model.domain.entity.*
import com.decade.practice.model.local.Account
import com.decade.practice.security.TokenCredentialService
import jakarta.persistence.EntityManager
import jakarta.persistence.OptimisticLockException
import jakarta.persistence.PersistenceContext
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.reactive.TransactionSynchronization.STATUS_COMMITTED
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager
import java.util.*

@Service
@Transactional(
      isolation = Isolation.READ_COMMITTED,
      propagation = Propagation.REQUIRES_NEW
)
class UserService(
      private val userRepo: UserRepository,
      private val adminRepo: AdminRepository,
      private val encoder: PasswordEncoder,
      private val credentialService: TokenCredentialService,
      private val accountListeners: List<AccountEventListener>,
) : SelfAwareBean(), UserOperations {

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

            accountListeners.forEach { listeners ->
                  listeners.beforeAccountCreated(user)
            }
            TransactionSynchronizationManager.registerSynchronization(
                  object : TransactionSynchronization {
                        override fun afterCompletion(status: Int) {
                              accountListeners.forEach { listeners ->
                                    listeners.afterAccountCreated(user, status == STATUS_COMMITTED)
                              }
                        }
                  })
            return user
      }

      override fun createOauth2User(
            username: String,
            name: String,
            picture: String?
      ): User? {
            val password = UUID.randomUUID().toString()
            val avatar = if (picture != null)
                  ImageSpec(picture, picture)
            else
                  DefaultAvatar
            return (self as UserService).create(
                  username,
                  password,
                  name, Date(),
                  MALE, avatar,
                  false
            )
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
      override fun update(id: UUID, password: String, modifierToken: String): User {
            val user = userRepo.getPessimisticWrite(id)
            credentialService.validate(modifierToken)
            user.password = encoder.encode(password)
            accountListeners.forEach { listeners ->
                  listeners.beforePasswordChanged(user)
            }
            TransactionSynchronizationManager.registerSynchronization(
                  object : TransactionSynchronization {
                        override fun afterCompletion(status: Int) {
                              accountListeners.forEach { listeners ->
                                    listeners.afterPasswordChanged(user, status == STATUS_COMMITTED)
                              }
                        }
                  })
            return user
      }

      override fun prepareAccount(details: UserDetails): Account {
            val user = userRepo.getPessimisticWrite(details.username)
            if (user.password != details.password) {
                  throw BadCredentialsException("Password miss matched")
            }
            val credential = credentialService.create(user)
            return Account(user, credential)
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
      return create(
            username,
            password,
            name,
            dob,
            gender,
            avatar,
            false
      )
}

@Throws(DataIntegrityViolationException::class)
fun UserOperations.create(
      username: String,
      password: String,
      usernameAsIdentifier: Boolean = false
): User {
      return create(
            username,
            password,
            username,
            Date(),
            MALE,
            DefaultAvatar,
            usernameAsIdentifier
      )
}