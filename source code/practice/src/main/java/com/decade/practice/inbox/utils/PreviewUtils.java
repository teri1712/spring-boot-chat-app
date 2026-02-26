package com.decade.practice.inbox.utils;

import com.decade.practice.inbox.domain.MessageState;
import com.decade.practice.inbox.domain.TextState;

import java.util.UUID;

import static com.decade.practice.inbox.domain.File.FILE_TYPE;
import static com.decade.practice.inbox.domain.Icon.ICON_TYPE;
import static com.decade.practice.inbox.domain.Image.IMAGE_TYPE;
import static com.decade.practice.inbox.domain.Preference.PREFERENCE_TYPE;
import static com.decade.practice.inbox.domain.Text.TEXT_TYPE;

public class PreviewUtils {
      public static String getPreviewContent(UUID owner, MessageState messageState) {
            boolean mine = owner.equals(messageState.getSenderId());
            String prefix = (mine ? "You: " : "");
            String content = "Wtf";
            switch (messageState.getMessageType()) {
                  case TEXT_TYPE:
                        content = ((TextState) messageState).getContent();
                        break;
                  case IMAGE_TYPE:
                        content = "has sent an image";
                        break;
                  case ICON_TYPE:
                        content = "has sent an icon";
                        break;
                  case PREFERENCE_TYPE:
                        content = "has updated preferences";
                        break;
                  case FILE_TYPE:
                        content = "has sent a file";
                        break;
                  default:
                        break;
            }
            return prefix + content;
      }
}
