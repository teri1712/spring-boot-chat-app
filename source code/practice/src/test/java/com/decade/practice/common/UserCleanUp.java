package com.decade.practice.common;

import com.decade.practice.users.application.ports.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;

@RequiredArgsConstructor
@TestComponent
public class UserCleanUp implements DataCleanUp {

    private final UserRepository users;

    @Override
    public void clean() {
        users.deleteAll();
    }
}
