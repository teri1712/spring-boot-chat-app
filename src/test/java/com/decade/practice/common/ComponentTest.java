package com.decade.practice.common;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.modulith.test.EnableScenarios;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.event.RecordApplicationEvents;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest
@ActiveProfiles("test")
@Import({ContainerConfigs.class, DatasetImportSelector.class, OIDCConfig.class, TestBeans.class})
@AutoConfigureMockMvc
@TestExecutionListeners(
    listeners = DatasetTestExecutionListener.class,
    mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
@EnableScenarios
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RecordApplicationEvents
public @interface ComponentTest {
    Class<? extends TestDataset>[] datasets() default {};
}
