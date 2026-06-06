package com.decade.practice.common;

import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;

@TestConfiguration
@ComponentScan(includeFilters = @ComponentScan.Filter(TestComponent.class))
public class DataCleanUpBeans {
}
