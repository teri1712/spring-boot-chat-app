package com.decade.practice.utils;

import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ErrorMessageMatcher implements ResultMatcher {
      private final String errorMessage;

      private ErrorMessageMatcher(String errorMessage) {
            this.errorMessage = errorMessage;
      }

      @Override
      public void match(MvcResult result) throws Exception {
            assertEquals(errorMessage, result.getResponse().getErrorMessage());
      }

      public static ErrorMessageMatcher errorMessage(String errorMessage) {
            TreeSet<Integer> set = new TreeSet<>();
            return new ErrorMessageMatcher(errorMessage);
      }
}
