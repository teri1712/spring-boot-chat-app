package com.decade.practice;


import com.decade.practice.common.BaseTestClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BootstrapTest extends BaseTestClass {

    @Test
    public void givenValidEnviroment_whenRunningSpringBootApp_thenNoError() {
        Assertions.assertTrue(true);
    }
}
