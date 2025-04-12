package com.decade.practice.database

import com.decade.practice.database.repository.AdminRepository
import com.decade.practice.database.repository.getOrNull
import com.decade.practice.model.entity.Admin
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.data.jpa.repository.config.EnableJpaRepositories


@Configuration(proxyBeanMethods = false)
@EntityScan("com.decade.practice.model")
@EnableJpaRepositories(basePackages = ["com.decade.practice.repository"])
class DatabaseConfig(
    @Value("\${admin.username}")
    private val admin_username: String,
    @Value("\${admin.password}")
    private val admin_password: String
) : ApplicationContextAware {

    private lateinit var appCtx: ApplicationContext

    @EventListener(ApplicationReadyEvent::class)
    fun onApplicationReady() {
        val adminRepo = appCtx.getBean(AdminRepository::class.java)
        if (adminRepo.getOrNull() == null) {
            adminRepo.save(Admin(admin_username, admin_password))
        }
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        appCtx = applicationContext
    }
}