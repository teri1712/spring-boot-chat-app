package com.decade.practice.ws

import com.decade.practice.model.entity.Chat
import com.decade.practice.model.entity.SeenEvent
import com.decade.practice.model.entity.TextEvent
import com.decade.practice.model.entity.User
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import org.junit.jupiter.api.Test
import java.util.*


class JsonBodyTest {

    @Test
    fun testText() {
        val me = User("Luffy", "Luffy", "Luffy")
        me.id = UUID.nameUUIDFromBytes("Luffy".toByteArray())

        val you = User("Nami", "Nami", "Nami")
        you.id = UUID.nameUUIDFromBytes("Nami".toByteArray())

        println(
            ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .writeValueAsString(
                    TextEvent(
                        if (me.id < you.id) Chat(me, you) else Chat(you, me),
                        you,
                        "It's cool"
                    )
                )
        )
    }

    @Test
    fun testSeen() {
        val me = User("Luffy", "Luffy", "Luffy")
        me.id = UUID.nameUUIDFromBytes("Luffy".toByteArray())

        val you = User("Nami", "Nami", "Nami")
        you.id = UUID.nameUUIDFromBytes("Nami".toByteArray())

        println(
            ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .writeValueAsString(
                    SeenEvent(
                        if (me.id < you.id) Chat(me, you) else Chat(you, me),
                        you,
                        at = System.currentTimeMillis()
                    )
                )
        )
    }

}