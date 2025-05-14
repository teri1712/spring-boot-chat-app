package com.decade.practice.utils;

import jakarta.servlet.http.HttpServletRequest;

public class PlatformsUtils {
      public static boolean isBrowserNavigation(HttpServletRequest request) {
            return request.getHeader("Accept").contains("text/html");
      }
}
