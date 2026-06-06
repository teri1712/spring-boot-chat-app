package com.decade.practice.bdd.config;

import com.decade.practice.common.DataCleanUpBeans;
import com.decade.practice.common.OIDCConfig;
import com.decade.practice.integration.BaseTestClass;
import com.decade.practice.integration.ContainerConfigs;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(scripts = "/sql/clean.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Import({ContainerConfigs.class, OIDCConfig.class, DataCleanUpBeans.class})
public class CucumberSpringConfig extends BaseTestClass {
}
