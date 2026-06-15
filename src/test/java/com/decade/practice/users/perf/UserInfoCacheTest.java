package com.decade.practice.users.perf;

import com.decade.practice.common.BaseTestClass;
import com.decade.practice.users.api.UserApi;
import com.decade.practice.users.api.UserInfo;
import com.decade.practice.users.application.ports.out.UserRepository;
import com.decade.practice.users.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@TestPropertySource(properties = {
    "redis.cache.enabled=true"
})
@Sql(value = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class UserInfoCacheTest extends BaseTestClass {

    @Autowired
    private UserRepository users;

    @Autowired
    private UserApi userApi;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    private List<UUID> seed() {
        List<User> seedUsers = IntStream.range(0, 1000).parallel().mapToObj(new IntFunction<User>() {
            @Override
            public User apply(int i) {
                return new User(
                    UUID.randomUUID(),
                    UUID.randomUUID().toString(),
                    UUID.randomUUID().toString(),
                    "vcl",
                    "vcl",
                    Instant.now(),
                    1f
                );
            }
        }).toList();
        users.saveAll(seedUsers);
        return seedUsers.stream().map(User::getId).toList();
    }

    @AfterEach
    @BeforeEach
    void cleanUp() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    @Sql({"/sql/clean.sql"})
    void given100000Users_whenFirstQueryUsers_thenExtremelySlow() throws Exception {
        List<UUID> userList = seed();
        Set<UUID> query = new HashSet<>(userList.subList(0, 500));

        Instant before, after;
        before = Instant.now();
        userApi.getUserInfo(query);
        after = Instant.now();
        Duration durationBefore = Duration.between(before, after);
        log.trace("Time duration for first query: {}millis", durationBefore.toMillis());

        before = Instant.now();
        Map<UUID, UserInfo> result = userApi.getUserInfo(query);
        after = Instant.now();
        result.forEach((k, v) -> {
            assertThat(v.id()).isNotNull();
            assertThat(v.username()).isNotNull();
            assertThat(v.name()).isEqualTo("vcl");
            assertThat(v.avatar()).isEqualTo("vcl");
        });
        Duration durationAfter = Duration.between(before, after);
        log.trace("Time duration for second query: {}millis", durationAfter.toMillis());

        assertThat(durationAfter).isLessThan(durationBefore);
    }

    @Test
    @Sql({"/sql/clean.sql"})
    void given800UsersAlreadyInCache_whenQueryWithAnother200User_thenStillFaster() throws Exception {
        List<UUID> userList = seed();
        Set<UUID> query1 = new HashSet<>(userList.subList(0, 900));
        Set<UUID> query2 = new HashSet<>(userList.subList(0, 1000));

        Instant before, after;
        before = Instant.now();
        userApi.getUserInfo(query1);
        after = Instant.now();
        Duration durationBefore = Duration.between(before, after);
        log.trace("Time duration for first query: {}millis", durationBefore.toMillis());

        before = Instant.now();
        Map<UUID, UserInfo> result = userApi.getUserInfo(query2);
        after = Instant.now();
        result.forEach((k, v) -> {
            assertThat(v.id()).isNotNull();
            assertThat(v.username()).isNotNull();
            assertThat(v.name()).isEqualTo("vcl");
            assertThat(v.avatar()).isEqualTo("vcl");
        });
        Duration durationAfter = Duration.between(before, after);
        log.trace("Time duration for second query: {}millis", durationAfter.toMillis());
        assertThat(durationAfter).isLessThan(durationBefore);
    }


}
