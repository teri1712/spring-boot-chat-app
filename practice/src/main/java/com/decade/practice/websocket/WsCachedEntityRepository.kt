package com.decade.practice.websocket

import com.decade.practice.database.repository.ChatRepository
import com.decade.practice.database.repository.UserRepository
import com.decade.practice.database.repository.get
import com.decade.practice.model.TypeEvent
import com.decade.practice.model.embeddable.ChatIdentifier
import com.decade.practice.model.entity.Chat
import com.decade.practice.model.entity.User
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.cache.RedisCacheWriter
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.fromSerializer
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.*

private const val TYPE_REPOSITORY_CACHE_MANAGER = "TYPE_REPOSITORY_CACHE_MANAGER"
private const val USER_KEYSPACE = "USER_ENTITIES_CACHE"
private const val CHAT_KEYSPACE = "CHAT_ENTITIES_CACHE"
private const val TYPE_KEYSPACE: String = "Typing"

@Configuration
@EnableCaching
class CachingConfig {

    fun cacheManager(connectionFactory: RedisConnectionFactory, seconds: Long): CacheManager {
        return RedisCacheManager.builder(RedisCacheWriter.lockingRedisCacheWriter(connectionFactory))
            .cacheDefaults(
                RedisCacheConfiguration.defaultCacheConfig()
                    .serializeValuesWith(
                        fromSerializer(GenericJackson2JsonRedisSerializer())
                    )
                    .entryTtl(Duration.ofSeconds(seconds))
            ).build()
    }

    @Bean(TYPE_REPOSITORY_CACHE_MANAGER)
    fun typeRepoCacheManager(connectionFactory: RedisConnectionFactory): CacheManager {
        return cacheManager(connectionFactory, 2)
    }

    @Bean
    @Primary
    fun cacheManager(connectionFactory: RedisConnectionFactory): CacheManager {
        return cacheManager(connectionFactory, 5 * 60L)
    }

}


@Component
class WsCachedEntityRepository(
    private val userRepo: UserRepository,
    private val chatRepo: ChatRepository
) : WsEntityRepository {

    @Cacheable(
        value = [USER_KEYSPACE],
        key = "#username"
    )
    override fun getUser(username: String): User =
        userRepo.getByUsername(username)


    @Cacheable(
        value = [CHAT_KEYSPACE],
        key = "#id.toString()"
    )
    override fun getChat(id: ChatIdentifier): Chat =
        chatRepo.get(id)


    @Cacheable(
        value = [TYPE_KEYSPACE],
        key = "T(com.decade.practice.model.TypeEventKt).determineKey(#from,#chat)",
        cacheManager = TYPE_REPOSITORY_CACHE_MANAGER,
        unless = "#result == null"
    )
    override fun getType(chat: ChatIdentifier, from: UUID, readOnly: Boolean): TypeEvent? {
        if (readOnly)
            return null
        return TypeEvent(from, chat)
    }

}