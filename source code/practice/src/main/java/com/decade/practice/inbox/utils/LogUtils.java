package com.decade.practice.inbox.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class LogUtils {

      public static final PageRequest SEQUENCE_DESC_PAGE = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "sequenceId"));
      public static final PageRequest SEQUENCE_ASC_PAGE = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "sequenceId"));

}