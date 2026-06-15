package com.decade.practice.common;

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
@RecordApplicationEvents
@Slf4j
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
@Import({TestBeans.class, ContainerConfigs.class, OIDCConfig.class, DataCleanUpBeans.class})
public abstract class BaseTestClass {

    @Autowired
    private TestDataset data;

    @BeforeEach
    void setUp() {
        data.setup();
    }

    @AfterEach
    void cleanUp() {
        data.clean();
    }
}
