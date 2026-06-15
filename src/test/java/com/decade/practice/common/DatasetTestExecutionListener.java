package com.decade.practice.common;

import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import java.util.Arrays;
import java.util.List;

public class DatasetTestExecutionListener extends AbstractTestExecutionListener {
    @Override
    public void beforeTestMethod(TestContext testContext) {
        getDatasets(testContext).forEach(TestDataset::setup);
    }

    @Override
    public void afterTestMethod(TestContext testContext) {
        getDatasets(testContext).forEach(TestDataset::clean);
    }

    private List<? extends TestDataset> getDatasets(TestContext testContext) {
        ComponentTest annotation = MergedAnnotations.from(testContext.getTestClass())
            .get(ComponentTest.class).synthesize();
        ApplicationContext context = testContext.getApplicationContext();
        return Arrays.stream(annotation.datasets())
            .map(context::getBean).toList();
    }
}
