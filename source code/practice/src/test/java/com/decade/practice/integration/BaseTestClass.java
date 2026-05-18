package com.decade.practice.integration;

import com.decade.practice.common.DataCleanUpBeans;
import com.decade.practice.common.OIDCConfig;
import com.decade.practice.common.TestDataSet;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.modulith.test.EnableScenarios;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.event.RecordApplicationEvents;


@EnableScenarios
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Slf4j
@AutoConfigureMockMvc
@ActiveProfiles("test")
@RecordApplicationEvents
@SpringBootTest
@Import({TestBeans.class, ContainerConfigs.class, OIDCConfig.class, DataCleanUpBeans.class})
public abstract class BaseTestClass {

    @Autowired
    private TestDataSet data;

    @BeforeEach
    void setUp() {
        data.setUp();
    }

    @AfterEach
    void cleanUp() {
        data.clean();
    }
}
