package com.decade.practice.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class EventUtils {

        private static final int EVENT_LIMIT = 20;
        public static final PageRequest EVENT_VERSION_LESS_THAN_EQUAL = PageRequest.of(0, EVENT_LIMIT, Sort.by(Sort.Direction.DESC, "receipt.eventVersion"));
        public static final PageRequest HEAD_EVENT = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "eventVersion"));

}