package com.decade.practice.session

import com.decade.practice.event.TransactionalCompletionAccountListener
import com.decade.practice.model.entity.User
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.session.SessionRegistry
import org.springframework.session.Session
import org.springframework.session.SessionRepository
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

class PasswordChangeRemoveSessionsAccountListener(
    private val sessionRegistry: SessionRegistry,
    private val sessionRepository: SessionRepository<out Session>
) : TransactionalCompletionAccountListener() {

    override fun handlePasswordChanged(account: User) {
        val currentSession =
            (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes)
                .request.getSession(false)
        if (currentSession != null) {
            val authentication = SecurityContextHolder.getContextHolderStrategy().context.authentication
            val sessions = sessionRegistry.getAllSessions(authentication.principal, true)
            for (session in sessions) {
                if (session.sessionId != currentSession.id) {
                    sessionRepository.deleteById(session.sessionId)
                }
            }
        }
    }
}