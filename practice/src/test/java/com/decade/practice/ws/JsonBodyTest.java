package com.decade.practice.ws;

import com.decade.practice.model.domain.entity.Chat;
import com.decade.practice.model.domain.entity.SeenEvent;
import com.decade.practice.model.domain.entity.TextEvent;
import com.decade.practice.model.domain.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;
import java.util.UUID;

public class JsonBodyTest {

    @Test
    public void testText() throws Exception {
        User me = new User("Luffy", "Luffy");
        me.setName("Luffy");
        me.setId(UUID.nameUUIDFromBytes("Luffy".getBytes()));

        User you = new User("Nami", "Nami");
        you.setName("Nami");
        you.setId(UUID.nameUUIDFromBytes("Nami".getBytes()));

        System.out.println(
            new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .writeValueAsString(
                    new TextEvent(
                        me.getId().compareTo(you.getId()) < 0 ? new Chat(me, you) : new Chat(you, me),
                        you,
                        "It's cool"
                    )
                )
        );
    }

    @Test
    public void testSeen() throws Exception {
        User me = new User("Luffy", "Luffy");
        me.setName("Luffy");
        me.setId(UUID.nameUUIDFromBytes("Luffy".getBytes()));

        User you = new User("Nami", "Nami");
        you.setName("Nami");
        you.setId(UUID.nameUUIDFromBytes("Nami".getBytes()));

        System.out.println(
            new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .writeValueAsString(
                    new SeenEvent(
                        me.getId().compareTo(you.getId()) < 0 ? new Chat(me, you) : new Chat(you, me),
                        you,
                        System.currentTimeMillis()
                    )
                )
        );
    }
}
