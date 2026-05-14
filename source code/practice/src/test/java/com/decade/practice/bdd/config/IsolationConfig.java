package com.decade.practice.bdd.config;

import com.decade.practice.common.TestDataSet;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class IsolationConfig {

    private final TestDataSet data;


    @Before
    public void setupDataSet() {
        data.setUp();
    }

    @After
    public void cleanDataSet() {
        data.clean();
    }

}
