package com.decade.practice.users.integration;

import com.decade.practice.common.TestDataset;
import com.decade.practice.users.application.ports.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
@RequiredArgsConstructor
public class UserDataset implements TestDataset {
    final UserRepository users;

    @Override
    public void setup() {
        this.clean();
    }

    @Override
    public void clean() {
        users.deleteAll();
    }
}
