package com.decade.practice.session

import com.decade.practice.event.AccountEventListener
import com.decade.practice.model.domain.entity.User
import com.decade.practice.security.TokenCredentialService
import com.decade.practice.util.TokenUtils
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.session.SessionRegistry
import org.springframework.session.Session
import org.springframework.session.SessionRepository
import org.springframework.transaction.support.TransactionSynchronizationManager
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

/**
 * Listens for password changes and invalidates related sessions and tokens.
 * Responsible for restoring invalidated tokens if the change operation fails.
 */
class PasswordChangeSessionInvalidator(
      private val sessionRegistry: SessionRegistry,
      private val sessionRepository: SessionRepository<out Session>,
      private val credentialService: TokenCredentialService
) : AccountEventListener {

      companion object {
            /**
             * A key used for storing invalidated tokens in a transaction context.
             */
            const val INVALIDATED_TOKENS = "INVALIDATED_TOKENS"

            /**
             * Retrieves the current HTTP request from the thread-bound context.
             */
            private val currentRequest: HttpServletRequest
                  get() = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request

            /**
             * Retrieves the current authentication details from the security context.
             */
            private val currentAuthentication
                  get() = SecurityContextHolder.getContextHolderStrategy().context.authentication
      }

      /**
       * Invoked before the password change operation is executed.
       * Invalidates tokens right away to prevent unauthorized reuse.
       */
      override fun beforePasswordChanged(account: User) {
            invalidateTokens()
      }

      /**
       * Invoked after the password change operation finishes.
       * Depending on the success, either sessions are invalidated and the current token is reactivated,
       * or previously invalidated tokens are restored if the operation fails.
       */
      override fun afterPasswordChanged(account: User, success: Boolean) {
            try {
                  if (success) {
                        invalidateSessions()
                        reactivateCurrentToken()
                  } else {
                        restoreTokens()
                  }
            } finally {
                  // Unbinds the invalidated tokens from the transaction if they're still present
                  if (TransactionSynchronizationManager.hasResource(INVALIDATED_TOKENS))
                        TransactionSynchronizationManager.unbindResource(INVALIDATED_TOKENS)
            }
      }

      /**
       * Invalidates all sessions belonging to the principal, except the current one.
       */
      private fun invalidateSessions() {
            val currentSession = currentRequest.getSession(false)
            val principal = currentAuthentication.principal
            for (session in sessionRegistry.getAllSessions(principal, true)) {
                  // Delete each session except the actively used one
                  if (session.sessionId != currentSession.id) {
                        sessionRepository.deleteById(session.sessionId)
                  }
            }
      }

      /**
       * Restores tokens that were invalidated if the password change operation did not succeed.
       */
      private fun restoreTokens() {
            val bound = TransactionSynchronizationManager.getResource(INVALIDATED_TOKENS) ?: return
            val deletedTokens = bound as List<String>
            val username = currentAuthentication.name

            // Add back any tokens that were previously removed
            deletedTokens.forEach { token ->
                  credentialService.add(username, token)
            }
      }

      /**
       * Re-adds the current token to the credential store after a successful password change.
       */
      private fun reactivateCurrentToken() {
            val username = currentAuthentication.name
            val refreshToken = TokenUtils.extractRefreshToken(currentRequest) ?: return

            // Re-add the current refresh token for continued use
            credentialService.add(username, refreshToken)
      }

      /**
       * Evicts all tokens belonging to the user and binds them to the transaction,
       * so they can be restored if the operation fails.
       */
      private fun invalidateTokens() {
            val username = currentAuthentication.name
            val deletedTokens = credentialService.evict(username)
            // Prepare for restoration in case the password change fails
            TransactionSynchronizationManager.bindResource(INVALIDATED_TOKENS, deletedTokens)
      }
}