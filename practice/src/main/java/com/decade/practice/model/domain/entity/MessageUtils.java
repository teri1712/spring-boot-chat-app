package com.decade.practice.model.domain.entity;

public class MessageUtils {
      
      public static final String TEXT = "TEXT";
      public static final String IMAGE = "IMAGE";
      public static final String ICON = "ICON";

      public static boolean isMessage(ChatEvent event) {
            String eventType = event.getEventType();
            return eventType.equals(TEXT)
                  || eventType.equals(IMAGE)
                  || eventType.equals(ICON);
      }
}