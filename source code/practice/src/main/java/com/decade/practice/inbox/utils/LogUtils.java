package com.decade.practice.inbox.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class LogUtils {

      public static final Sort SEQUENCE_SORT_DESC = Sort.by(Sort.Direction.DESC, "sequenceId");

      public static final PageRequest SEQUENCE_LESS_THAN_EQUAL = PageRequest.of(0, 20, SEQUENCE_SORT_DESC);

}