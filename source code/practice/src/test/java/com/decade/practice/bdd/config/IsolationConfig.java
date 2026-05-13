package com.decade.practice.bdd.config;

import com.decade.practice.common.DataCleanUp;
import io.cucumber.java.After;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class IsolationConfig {

    private final DataCleanUp data;

    @After
    public void cleanUpDocs() {
        data.clean();
    }

}
