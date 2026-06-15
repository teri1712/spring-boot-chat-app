package com.decade.practice.live.integration;

import com.decade.practice.common.ComponentTest;
import com.decade.practice.common.RedisDataset;
import org.springframework.boot.test.context.SpringBootTest;

@ComponentTest(
    datasets = {
        RedisDataset.class
    }
)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BaseLiveTest {
}
