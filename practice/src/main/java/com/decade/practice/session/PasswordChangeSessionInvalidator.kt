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

class PasswordChangeSessionInvalidator(
      private val sessionRegistry: SessionRegistry,
      private val sessionRepository: SessionRepository<out Session>,
      private val credentialService: TokenCredentialService
) : AccountEventListener {

      companion object {
            const val INVALIDATED_TOKENS = "INVALIDATED_TOKENS"
            private val currentRequest: HttpServletRequest
                  get() = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes)
                        .request
            private val currentAuthentication
                  get() = SecurityContextHolder.getContextHolderStrategy()
                        .context
                        .authentication
      }

      override fun beforePasswordChanged(account: User) {
            invalidateTokens()
      }

      override fun afterPasswordChanged(account: User, success: Boolean) {
            try {
                  if (success) {
                        invalidateSessions()
                        reactivateCurrentToken()
                  } else {
                        restoreTokens()
                  }
            } finally {
                  if (TransactionSynchronizationManager.hasResource(INVALIDATED_TOKENS))
                        TransactionSynchronizationManager.unbindResource(INVALIDATED_TOKENS)
            }
      }

      private fun invalidateSessions() {
            val currentSession = currentRequest.getSession(false)
            val principal = currentAuthentication.principal
            for (session in sessionRegistry.getAllSessions(principal, true)) {
                  if (session.sessionId != currentSession.id) {
                        sessionRepository.deleteById(session.sessionId)
                  }
            }
      }

      private fun restoreTokens() {
            val bound =
                  TransactionSynchronizationManager.getResource(INVALIDATED_TOKENS) ?: return
            val deletedTokens = bound as List<String>
            val username = currentAuthentication.name
            deletedTokens.forEach { token ->
                  credentialService.add(username, token)
            }
      }

      private fun reactivateCurrentToken() {
            val username = currentAuthentication.name
            val refreshToken =
                  TokenUtils.extractRefreshToken(currentRequest) ?: return

            credentialService.add(username, refreshToken)

      }

      private fun invalidateTokens() {
            val username = currentAuthentication.name
            val deletedTokens = credentialService.evict(username)
            //prepare for restoration on failures
            TransactionSynchronizationManager.bindResource(INVALIDATED_TOKENS, deletedTokens)
      }
}