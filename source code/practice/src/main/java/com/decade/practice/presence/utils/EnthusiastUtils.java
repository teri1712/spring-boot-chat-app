package com.decade.practice.presence.utils;

public class EnthusiastUtils {
      public static final String ENTHUSIAST_KEY = "enthusiasts";

      public static String determineEnthusiastId(String chatId) {
            return ENTHUSIAST_KEY + ":" + chatId;
      }

}
